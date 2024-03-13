package com.kk.ddd.support.fsm;

/**
 * 状态机事件处理器接口 <br>
 *
 * @author KaiKoo
 */
public interface FsmEventProcessor<E extends FsmEvent, T, C extends FsmContext<E, T>> {

  void process(C context) throws Throwable;
}
