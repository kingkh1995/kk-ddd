package com.kk.ddd.support.fsm;

/**
 * 处理器处理流程拆分接口 <br>
 *
 * @author KaiKoo
 */
public interface FsmEventProcessStep<E extends FsmEvent, T, C extends FsmContext<E, T>> {

  /**
   * 处理前准备（主要是查询数据）及上下文校验，横向拆分流程，并复用校验器 <br>
   * check args -> build context - > check context -> async check context
   */
  void prepare(C context);

  /** 获取当前状态处理器处理完毕后，所处于的下一个状态 */
  String getDestState(C context);

  /** 状态动作方法，主要状态流转逻辑，需要处理异常。 */
  void action(String destState, C context) throws Throwable;

  /** 状态数据持久化 */
  void save(String destState, C context);

  /** 状态迁移成功，持久化后执行的后续处理（如发送消息通知） */
  default void after(C context) {}
}
