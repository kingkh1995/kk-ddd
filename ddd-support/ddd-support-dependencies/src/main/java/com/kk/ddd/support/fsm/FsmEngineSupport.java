package com.kk.ddd.support.fsm;

import com.kk.ddd.support.util.ApplicationContextAwareSingleton;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * 状态机引擎：基于某些特定业务和场景下，根据源状态和发生的事件，来执行下一步的流程处理逻辑，并设置一个目标状态。 状态机引擎支持类 <br>
 * 使用步骤： <br>
 * 0、定义Event枚举（建议） <br>
 * 1、定义FsmEvent实现基类 <br>
 * 2、定义FsmContext实现类 <br>
 * 3、定义FsmEventProcessor基类，在基类基础上，再对有相似流程的处理器提取出基类，并添加拓展点 <br>
 * 4、定义FsmEngine实现类作为调用的入口，实现buildContext，需要被ioc容器管理 <br>
 * 5、定义Checker工具类，至少要将可公用的校验器在工具类中作为静态变量定义，其他非公用的可以使用lambda表达式实现 <br>
 * 6、定义事件处理器类，并添加@EventProcessor注解，实现action或者上层基类定义的拓展点，指定校验器 <br>
 * 7、定义特定事件类型，调用端通过FsmEngine实现类发起事件，在对应的处理器类将事件对象由基类转为特定的类型并处理。
 *
 * @author KaiKoo
 */
@Slf4j
public abstract class FsmEngineSupport<
        E extends FsmEvent, T, C extends FsmContext<E, T>, P extends FsmEventProcessor<E, T, C>>
    extends ApplicationContextAwareSingleton implements FsmEngine<E, T> {

  @Override
  public void sendEvent(E event) throws Exception {
    if (event.newCreate()) {
      throw new FsmEngineException("Entity can't be null!");
    }
    this.sendEvent(event, this.findEntity(event.getEntityId()));
  }

  protected abstract T findEntity(String entityId);

  @Override
  public void sendEvent(E event, T entity) throws Exception {
    // 构造当前事件上下文
    var context = this.buildContext(event, entity);
    // 获取当前事件处理器
    var processor = this.getEventProcessor(context);
    // 执行处理逻辑
    processor.process(context);
  }

  protected abstract C buildContext(E event, T entity);

  private P getEventProcessor(C context) {
    // 查询事件处理器集合
    var processorList =
        this.acquireEventProcessor(
                context.getEvent().getEventType(),
                context.getState(),
                context.getBiz(),
                context.getScene())
            .orElseThrow(() -> new FsmEngineException("No processor found!"));
    log.info(
        "processorList:{}",
        processorList.stream()
            .map(p -> p.getClass().getSimpleName())
            .collect(Collectors.joining(", ", "[", "]")));
    if (processorList.size() > 1) {
      throw new FsmEngineException("More than one processor found!");
    }
    return processorList.get(0);
  }

  // ===============================================================================================
  /** 以下是EventProcessor管理相关 */
  protected abstract Class<P> getPClass();

  /** 事件处理器实现类三层map */
  private Map<String, Map<String, Map<String, List<P>>>> processorMap;

  private static final String[] DEFAULT = new String[] {"#"};

  @Override
  public void afterSingletonsInstantiated() {
    if (this.processorMap != null) {
      return;
    }
    // 使用HashMap和LinkedList，因为只会有并发get操作。
    this.processorMap = new HashMap<>();
    // 因为@EventProcessor是多个业务公用的，所以不能通过注解获取，需要根据业务的EventProcessor基类class去获取。
    for (P p : this.getApplicationContext().getBeansOfType(this.getPClass()).values()) {
      var eventProcessor = p.getClass().getAnnotation(EventProcessor.class);
      if (eventProcessor == null) {
        continue;
      }
      // 第一层key是事件
      this.processorMap.compute(
          eventProcessor.event(),
          (k1, eventMap) -> {
            if (eventMap == null) {
              eventMap = new HashMap<>();
            }
            // 第二层key是状态
            String[] states = eventProcessor.state().length == 0 ? DEFAULT : eventProcessor.state();
            for (String state : states) {
              eventMap.compute(
                  state,
                  (k2, stateMap) -> {
                    if (stateMap == null) {
                      stateMap = new HashMap<>();
                    }
                    // 第三层key是具体的业务场景
                    // 因为处理器数量会非常多，开发过程中难免会重复定义，收集为list而不是直接覆盖便于排查问题和后序升级
                    String[] bizs =
                        eventProcessor.biz().length == 0 ? DEFAULT : eventProcessor.biz();
                    String[] scenes =
                        eventProcessor.scene().length == 0 ? DEFAULT : eventProcessor.scene();
                    for (String biz : bizs) {
                      for (String scene : scenes) {
                        String key = biz + "@" + scene;
                        stateMap.compute(
                            key,
                            (k3, list) -> {
                              if (list == null) {
                                // 使用LinkedList，为了节约空间，因为正常情况下元素只有一个。
                                list = new LinkedList<>();
                              }
                              list.add(p);
                              return list;
                            });
                      }
                    }
                    return stateMap;
                  });
            }
            return eventMap;
          });
    }
  }

  private Optional<List<P>> acquireEventProcessor(
      String eventType, String state, String biz, String scene) {
    log.info("eventType '{}', state '{}', biz '{}', scene '{}'.", eventType, state, biz, scene);
    return Optional.ofNullable(
            // event level
            processorMap.get(eventType))
        .map(
            // state level
            eventMap -> Optional.ofNullable(eventMap.get(state)).orElse(eventMap.get("#")))
        .map(
            // biz + scene level
            stateMap -> {
              var op = Optional.<List<P>>empty();
              if (biz != null && scene != null) {
                op.or(() -> Optional.ofNullable(stateMap.get(biz + "@" + scene)));
              }
              if (biz != null) {
                op.or(() -> Optional.ofNullable(stateMap.get(biz + "@#")));
              }
              if (scene != null) {
                op.or(() -> Optional.ofNullable(stateMap.get("#@" + scene)));
              }
              return op.or(() -> Optional.ofNullable(stateMap.get("#@#"))).orElse(null);
            });
  }

  public static class FsmEngineException extends RuntimeException {

    FsmEngineException(String message) {
      super(message);
    }
  }
}
