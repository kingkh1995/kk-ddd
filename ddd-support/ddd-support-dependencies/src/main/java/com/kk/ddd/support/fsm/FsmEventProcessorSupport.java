package com.kk.ddd.support.fsm;

import com.kk.ddd.support.util.task.TaskFlow;
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
  protected final FsmPrepareTaskFlow<E, T, C> prepareTaskFlow;

  /** 执行阶段任务容器，需要自定义任务顺序。 */
  protected final TaskFlow<C> actionTaskFlow;

  public FsmEventProcessorSupport(TaskFlow<C> actionTaskFlow) {
    this(FsmPrepareTaskFlow.empty(), actionTaskFlow);
  }

  public FsmEventProcessorSupport(
      FsmPrepareTaskFlow<E, T, C> prepareTaskFlow, TaskFlow<C> actionTaskFlow) {
    this.prepareTaskFlow = prepareTaskFlow;
    this.actionTaskFlow = actionTaskFlow;
  }

  @Override
  public void process(C context) throws Throwable {
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

  @Override
  public void action(String destState, C context) throws Throwable {
    this.actionTaskFlow.apply(context).get();
  }

  protected void checkArgs(C context) {
    Optional.ofNullable(prepareTaskFlow.getArgsChecker())
        .ifPresent(container -> container.sequential().apply(context).join());
  }

  protected void buildContext(C context) {
    Optional.ofNullable(prepareTaskFlow.getContextBuilder())
        .ifPresent(container -> container.parallel().apply(context).join());
  }

  protected void checkContext(C context) {
    Optional.ofNullable(prepareTaskFlow.getContextChecker())
        .ifPresent(container -> container.sequential().apply(context).join());
  }

  protected void asyncCheckContext(C context) {
    Optional.ofNullable(prepareTaskFlow.getContextAsyncChecker())
        .ifPresent(container -> container.parallel().apply(context).join());
  }

  @Override
  public void save(String destState, C context) {
    this.saveEntity(context.getEntity());
  }

  protected abstract void saveEntity(T entity);
}
