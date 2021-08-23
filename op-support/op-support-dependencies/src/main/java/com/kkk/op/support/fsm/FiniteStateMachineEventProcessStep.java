package com.kkk.op.support.fsm;

import com.kkk.op.support.base.Entity;

/**
 * <br>
 *
 * @author KaiKoo
 */
public interface FiniteStateMachineEventProcessStep<
    E extends FiniteStateMachineEvent,
    T extends Entity,
    C extends FiniteStateMachineContext<E, T>> {

  /**
   * 准备数据，包括参数校验 <br>
   * 横向拆分 paramChecker -> prepare - > syncChecker -> asyncChecker
   */
  void prepareAndCheck(C context) throws Exception;

  /** 获取当前状态处理器处理完毕后，所处于的下一个状态 */
  String getDestState(C context);

  /** 状态动作方法，主要状态流转逻辑 */
  void action(String destState, C context) throws Exception;

  /** 状态数据持久化 */
  void save(String destState, C context);

  /** 状态迁移成功，持久化后执行的后续处理（如发送消息通知） */
  default void after(C context) {}
}
