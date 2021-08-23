package com.kkk.op.support.fsm;

import com.kkk.op.support.base.Entity;
import java.util.Collections;
import java.util.List;

/**
 * 校验器合集接口 <br>
 *
 * @author KaiKoo
 */
public interface Checkable<
    E extends FiniteStateMachineEvent,
    T extends Entity,
    C extends FiniteStateMachineContext<E, T>> {

  /** 参数校验 */
  default List<Checker<E, T, C>> getParamChecker() {
    return Collections.EMPTY_LIST;
  }
  /** 需同步执行的状态检查器 */
  default List<Checker<E, T, C>> getSyncChecker() {
    return Collections.EMPTY_LIST;
  }
  /** 可异步执行的校验器 */
  default List<Checker<E, T, C>> getAsyncChecker() {
    return Collections.EMPTY_LIST;
  }
}
