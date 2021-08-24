package com.kkk.op.support.fsm;

import com.kkk.op.support.base.Entity;

/**
 * 校验器函数式接口 <br>
 *
 * @author KaiKoo
 */
public interface Checker<E extends FsmEvent, T extends Entity, C extends FsmContext<E, T>> {

  CheckResult check(C context);
}
