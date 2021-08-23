package com.kkk.op.support.fsm;

import com.kkk.op.support.base.Entity;

/**
 * 校验器接口 <br>
 *
 * @author KaiKoo
 */
public interface Checker<
    E extends FiniteStateMachineEvent,
    T extends Entity,
    C extends FiniteStateMachineContext<E, T>> {

  CheckResult check(C context);

  /** 多个checker时的执行顺序 */
  default int order() {
    return 0;
  }
}
