package com.kkk.op.support.fsm;

import com.kkk.op.support.base.Entity;
import java.util.List;

/**
 * 状态机引擎基类 <br>
 *
 * @author KaiKoo
 */
public abstract class FiniteStateMachineEngineSupport<
        E extends FiniteStateMachineEvent,
        T extends Entity,
        C extends FiniteStateMachineContext<E, T>,
        P extends FiniteStateMachineEventProcessor<E, T, C>>
    implements FiniteStateMachineEngine<E, T> {

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
    // 实体当前状态不匹配事件要求状态抛出异常
    if (!context.getEvent().matches(context.getState())) {
      throw new RuntimeException("current state not equal to the event matching state!");
    }
    // 查询事件处理器集合
    var processorList =
        this.acquireEventProcessor(
            context.getEvent().getEventId(),
            context.getState(),
            context.getBiz(),
            context.getScene());
    if (processorList.isEmpty()) {
      throw new RuntimeException("no processor found!");
    }
    if (processorList.size() > 1) {
      throw new RuntimeException("more than one processor found!");
    }
    return processorList.get(0);
  }

  protected abstract List<P> acquireEventProcessor(
      String eventId, String state, String biz, String scene);
}
