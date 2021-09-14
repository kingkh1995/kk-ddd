package com.kkk.op.support.fsm;

/**
 * 校验器函数式接口 <br>
 *
 * @author KaiKoo
 */
@FunctionalInterface
public interface Checker<E extends FsmEvent, T, C extends FsmContext<E, T>> {

  CheckResult check(C context);
}
