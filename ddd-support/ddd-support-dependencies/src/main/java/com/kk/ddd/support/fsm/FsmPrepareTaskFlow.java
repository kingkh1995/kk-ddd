package com.kk.ddd.support.fsm;

import com.kk.ddd.support.util.task.TaskFlow;

/**
 * 准备及校验阶段任务容器，将通用的函数式接口全部定义为静态常量。 <br>
 *
 * @author KaiKoo
 */
public interface FsmPrepareTaskFlow<E extends FsmEvent, T, C extends FsmContext<E, T>> {

  @SuppressWarnings("rawtypes")
  FsmPrepareTaskFlow EMPTY = new FsmPrepareTaskFlow() {};

  @SuppressWarnings("unchecked")
  static <E extends FsmEvent, T, C extends FsmContext<E, T>> FsmPrepareTaskFlow<E, T, C> empty() {
    return (FsmPrepareTaskFlow<E, T, C>) EMPTY;
  }

  /** 参数校验，同步执行。 */
  default TaskFlow<C> getArgsChecker() {
    return null;
  }

  /** 数据准备，异步执行。 */
  default TaskFlow<C> getContextBuilder() {
    return null;
  }

  /** 需同步执行的校验器 */
  default TaskFlow<C> getContextChecker() {
    return null;
  }

  /** 可异步执行的校验器 */
  default TaskFlow<C> getContextAsyncChecker() {
    return null;
  }
}
