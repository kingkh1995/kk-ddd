package com.kkk.op.support.fsm;

/**
 * 事件处理器支持类 <br>
 * 将处理流程纵向拆分（业务编排）：process ==> prepare -> check -> getDestState -> action -> save -> after <br>
 * 将核心处理逻辑横行拆分（逻辑复用）：具有相似逻辑的处理器，针对action方法增加拓展点让子类实现。
 *
 * @author KaiKoo
 */
public abstract class FsmEventProcessorSupport<E extends FsmEvent, T, C extends FsmContext<E, T>>
    implements FsmEventProcessor<E, T, C>, FsmEventProcessStep<E, T, C> {

  @Override
  public void process(C context) throws Exception {
    // 准备和校验
    this.prepareAndCheck(context);
    // 获取流转目标状态，getNextState需要prepare作为前置，因为部分情况下nextState转换自prepare之后的数据
    var destState = this.getDestState(context);
    // 核心业务逻辑
    this.action(destState, context);
    // 数据持久化
    this.save(destState, context);
    // 后置操作
    this.after(context);
  }

  @Override
  public void prepareAndCheck(C context) {
    var checkable = this.getCheckable();
    // 第一步参数校验（简单的参数校验，fail-fast机制）
    CheckerExecutor.serialCheck(checkable.getParamChecker(), context).throwIfFail();
    // 第二步数据准备（查询数据并补充上下文）
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
