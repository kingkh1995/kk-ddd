package com.kk.ddd.support.fsm;

import java.util.Optional;

/**
 * 事件处理器支持类 <br>
 * 将处理流程纵向拆分（业务编排）：process ==> prepare -> check -> getDestState -> action -> save -> after <br>
 * 将核心处理逻辑横行拆分（逻辑复用）：具有相似逻辑的处理器，针对action方法增加拓展点让子类实现。
 *
 * @author KaiKoo
 */
public abstract class FsmEventProcessorSupport<E extends FsmEvent, T, C extends FsmContext<E, T>>
    implements FsmEventProcessor<E, T, C>, FsmEventProcessStep<E, T, C> {

  /** 准备阶段任务容器，需要自定义任务顺序。 */
  protected final PrepareTaskContainer<E, T, C> prepareTaskContainer;

  public FsmEventProcessorSupport() {
    this(PrepareTaskContainer.empty());
  }

  public FsmEventProcessorSupport(PrepareTaskContainer<E, T, C> prepareTaskContainer) {
    this.prepareTaskContainer = prepareTaskContainer;
  }

  @Override
  public void process(C context) throws Exception {
    // 准备
    this.prepare(context);
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
  public void prepare(C context) {
    // 第一步参数校验（简单的参数校验，fail-fast）
    checkArgs(context);
    // 第二步数据准备（查询数据并构建上下文）
    buildContext(context);
    // 第三步同步校验
    checkContext(context);
    // 第四步异步校验（注意防止出现并发问题）
    asyncCheckContext(context);
  }

  protected void checkArgs(C context) {
    Optional.ofNullable(prepareTaskContainer.getArgsChecker())
        .ifPresent(container -> container.execute(context).throwIfFail());
  }

  protected void buildContext(C context) {
    Optional.ofNullable(prepareTaskContainer.getContextBuilder())
        .ifPresent(container -> container.execute(context).throwIfFail());
  }

  protected void checkContext(C context) {
    Optional.ofNullable(prepareTaskContainer.getContextChecker())
        .ifPresent(container -> container.execute(context).throwIfFail());
  }

  protected void asyncCheckContext(C context) {
    Optional.ofNullable(prepareTaskContainer.getContextAsyncChecker())
        .ifPresent(container -> container.execute(context).throwIfFail());
  }

  @Override
  public void save(String destState, C context) {
    this.saveEntity(context.getEntity());
  }

  protected abstract void saveEntity(T entity);
}
