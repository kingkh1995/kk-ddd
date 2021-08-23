package com.kkk.op.support.fsm;

import com.kkk.op.support.base.Entity;

/**
 * 状态机事件处理器接口 <br>
 *
 * @author KaiKoo
 */
public interface FiniteStateMachineEventProcessor<
    E extends FiniteStateMachineEvent,
    T extends Entity,
    C extends FiniteStateMachineContext<E, T>> {

  void process(C context) throws Exception;
}
