package com.kkk.op.support.fsm;

import com.kkk.op.support.base.Entity;

/**
 * 将处理逻辑process纵向拆分：prepare -> check -> getDestState -> action -> save -> after
 *
 * @author KaiKoo
 */
public abstract class FiniteStateMachineEventProcessorSupport<
        E extends FiniteStateMachineEvent,
        T extends Entity,
        C extends FiniteStateMachineContext<E, T>>
    implements FiniteStateMachineEventProcessor<E, T, C>,
        FiniteStateMachineEventProcessStep<E, T, C>,
        Checkable<E, T, C> {

  @Override
  public void process(C context) throws Exception {
    // 数据准备和校验
    this.prepareAndCheck(context);
    // getNextState不能在prepare前，因为有的nextState是根据prepare中的数据转换而来
    var destState = this.getDestState(context);
    // 业务逻辑
    this.action(destState, context);
    // 持久化
    this.save(destState, context);
    // after
    this.after(context);
  }

  @Override
  public void prepareAndCheck(C context) throws Exception {
    var checkable = this.getCheckable();
    // 第一步参数校验
    CheckerExecutor.serialCheck(checkable.getParamChecker(), context).throwIfFail();
    // 第二步数据准备
    this.prepare(context);
    // 第三步同步校验
    CheckerExecutor.serialCheck(checkable.getSyncChecker(), context).throwIfFail();
    // 第四步异步校验
    CheckerExecutor.parallelCheck(checkable.getAsyncChecker(), context).throwIfFail();
  }

  /** 获取校验器合集 */
  protected Checkable<E, T, C> getCheckable() {
    return new Checkable<>() {};
  }

  protected abstract void prepare(C context);

  @Override
  public void save(String destState, C context) {
    this.saveEntity(context.getEntity());
  }

  protected abstract void saveEntity(T entity);
}
