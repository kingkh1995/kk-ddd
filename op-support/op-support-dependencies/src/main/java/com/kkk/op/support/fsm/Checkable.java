package com.kkk.op.support.fsm;

import java.util.Collections;
import java.util.List;

/**
 * 校验器合集接口，可以定义Checkers工具类，将通用的校验器函数式接口全部定义为静态常量。 <br>
 *
 * @author KaiKoo
 */
public interface Checkable<E extends FsmEvent, T, C extends FsmContext<E, T>> {

  /** 参数校验 */
  default List<Checker<E, T, C>> getParamChecker() {
    return Collections.emptyList();
  }

  /** 需同步执行的状态校验器 */
  default List<Checker<E, T, C>> getSyncChecker() {
    return Collections.emptyList();
  }

  /** 可异步执行的校验器 */
  default List<Checker<E, T, C>> getAsyncChecker() {
    return Collections.emptyList();
  }
}
