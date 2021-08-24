package com.kkk.op.support.fsm;

import com.kkk.op.support.base.Entity;

/**
 * 状态机事件处理器接口 <br>
 *
 * @author KaiKoo
 */
public interface FsmEventProcessor<
    E extends FsmEvent, T extends Entity, C extends FsmContext<E, T>> {

  void process(C context) throws Exception;
}
