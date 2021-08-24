package com.kkk.op.support.fsm;

import java.util.Collections;
import java.util.List;

/**
 * 校验器合集接口，可以定义Checkers工具类，将校验器函数式接口全部定义为静态常量，从工具类中取值即可。 <br>
 *
 * @author KaiKoo
 */
public interface Checkable<E extends FsmEvent, T, C extends FsmContext<E, T>> {

  /** 参数校验 */
  default List<Checker<E, T, C>> getParamChecker() {
    return Collections.EMPTY_LIST;
  }
  /** 需同步执行的状态校验器 */
  default List<Checker<E, T, C>> getSyncChecker() {
    return Collections.EMPTY_LIST;
  }
  /** 可异步执行的校验器 */
  default List<Checker<E, T, C>> getAsyncChecker() {
    return Collections.EMPTY_LIST;
  }
}
