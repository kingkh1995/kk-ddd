package com.kkk.op.support.fsm;

import com.kkk.op.support.base.Entity;

/**
 * 处理器处理流程接口 <br>
 *
 * @author KaiKoo
 */
public interface FsmEventProcessStep<
    E extends FsmEvent, T extends Entity, C extends FsmContext<E, T>> {

  /**
   * 处理前准备（主要是查询数据）及上下文检查，横向拆分流程，并复用检查器 <br>
   * paramChecker -> prepare - > syncChecker -> asyncChecker
   */
  void prepareAndCheck(C context);

  /** 获取当前状态处理器处理完毕后，所处于的下一个状态 */
  String getDestState(C context);

  /** 状态动作方法，主要状态流转逻辑，需要处理异常。 */
  void action(String destState, C context) throws Exception;

  /** 状态数据持久化 */
  void save(String destState, C context);

  /** 状态迁移成功，持久化后执行的后续处理（如发送消息通知） */
  default void after(C context) {}
}
