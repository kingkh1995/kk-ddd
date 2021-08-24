package com.kkk.op.support.fsm;

import com.kkk.op.support.annotations.EventProcessor;
import com.kkk.op.support.base.ApplicationContextAwareBean;
import com.kkk.op.support.base.Entity;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * 状态机引擎基类 <br>
 * 流程引擎使用要求： <br>
 * 0、定义Event枚举（建议） <br>
 * 1、定义FsmEvent实现类 <br>
 * 2、定义FsmContext实现类 <br>
 * 3、定义FsmEventProcessor基类，在基类基础上，再对有相似流程的处理器提取出基类，并添加拓展点 <br>
 * 4、定义FsmEngine实现类作为调用的入口，实现buildContext，需要被ioc容器管理 <br>
 * 5、定义Checker工具类，至少要将可公用的检查器在工具类中作为静态变量定义，其他非公用的可以使用lambda表达式实现 <br>
 * 6、定义事件处理器类，并添加@EventProcessor注解，实现action或者上层基类定义的拓展点，指定检查器 <br>
 *
 * @author KaiKoo
 */
@Slf4j
public abstract class FsmEngineSupport<
        E extends FsmEvent,
        T extends Entity,
        C extends FsmContext<E, T>,
        P extends FsmEventProcessor<E, T, C>>
    extends ApplicationContextAwareBean implements FsmEngine<E, T> {

  @Override
  public void sendEvent(E event) throws Exception {
    if (event.newCreate()) {
      throw new RuntimeException("entity cannot be null!");
    }
    this.sendEvent(event, this.findEnity(event.getEntityId()));
  }

  protected abstract T findEnity(String eventId);

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
            context.getEvent().getEventId(),
            context.getState(),
            context.getBiz(),
            context.getScene());
    log.info(
        "processorList:{}",
        processorList.stream()
            .map(p -> p.getClass().getSimpleName())
            .collect(Collectors.joining(", ", "[", "]")));
    if (processorList.isEmpty()) {
      throw new RuntimeException("no processor found!");
    }
    if (processorList.size() > 1) {
      throw new RuntimeException("more than one processor found!");
    }
    return processorList.get(0);
  }

  /** EventProcessor管理相关 */
  private Class<P> pClass;

  {
    var actualTypeArguments =
        ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
    this.pClass = (Class<P>) actualTypeArguments[3];
  }

  private Map<String, Map<String, Map<String, List<P>>>> processorMap;

  @Override
  public void afterPropertiesSet() throws Exception {
    if (this.processorMap != null) {
      return;
    }
    this.processorMap = new HashMap<>();
    // 因为EventProcessor是公用的，所以不能使用getBeansWithAnnotation获取，需要按对应业务的EventProcessor基类类型去获取。
    for (P p : this.getApplicationContext().getBeansOfType(pClass).values()) {
      var eventProcessor = p.getClass().getAnnotation(EventProcessor.class);
      if (eventProcessor == null) {
        continue;
      }
      // 第一层key是事件
      String eventId = eventProcessor.eventId();
      this.processorMap.compute(
          eventId,
          (k1, eventMap) -> {
            if (eventMap == null) {
              eventMap = new HashMap<>();
            }
            // 第二层key是状态
            String[] states =
                eventProcessor.state().length == 0 ? new String[] {"#"} : eventProcessor.state();
            for (String state : states) {
              eventMap.compute(
                  state,
                  (k2, stateMap) -> {
                    if (stateMap == null) {
                      stateMap = new HashMap<>();
                    }
                    // 第三层key是具体的业务场景，因为处理器数量会非常多，开发过程中难免会重复定义，收集为list而不是直接覆盖便于排查问题。
                    String[] bizs =
                        eventProcessor.biz().length == 0
                            ? new String[] {"#"}
                            : eventProcessor.biz();
                    String[] scenes =
                        eventProcessor.scene().length == 0
                            ? new String[] {"#"}
                            : eventProcessor.scene();
                    for (String biz : bizs) {
                      for (String scene : scenes) {
                        String key = biz + "@" + scene;
                        stateMap.compute(
                            key,
                            (k3, list) -> {
                              if (list == null) {
                                list = new ArrayList<>();
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

  private List<P> acquireEventProcessor(String eventId, String state, String biz, String scene) {
    log.info("eventId:{}, state:{}, biz:{}, scene:{}", eventId, state, biz, scene);
    // event level
    var eventMap = this.processorMap.get(eventId);
    if (eventMap == null) {
      return Collections.EMPTY_LIST;
    }
    // state level
    var stateMap = eventMap.containsKey(state) ? eventMap.get(state) : eventMap.get("#");
    if (stateMap == null) {
      return Collections.EMPTY_LIST;
    }
    // biz + scene level
    if (biz != null && scene != null && stateMap.containsKey(biz + "@" + scene)) {
      return stateMap.get(biz + "@" + scene);
    }
    if (biz != null && stateMap.containsKey(biz + "@#")) {
      return stateMap.get(biz + "@#");
    }
    if (scene != null && stateMap.containsKey("#@" + scene)) {
      return stateMap.get("#@" + scene);
    }
    return stateMap.get("#@#");
  }
}
