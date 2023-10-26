package com.kk.ddd.support.fsm;

import com.kk.ddd.support.util.task.AsyncTaskContainer;
import com.kk.ddd.support.util.task.TaskContainer;

/**
 * 准备及校验阶段任务容器，使用TaskContainers，将通用的函数式接口全部定义为静态常量。 <br>
 *
 * @author KaiKoo
 */
public interface PrepareTaskContainer<E extends FsmEvent, T, C extends FsmContext<E, T>> {

  @SuppressWarnings("rawtypes")
  PrepareTaskContainer EMPTY = new PrepareTaskContainer() {};

  @SuppressWarnings("unchecked")
  static <E extends FsmEvent, T, C extends FsmContext<E, T>> PrepareTaskContainer<E, T, C> empty() {
    return (PrepareTaskContainer<E, T, C>) EMPTY;
  }

  /** 参数校验，同步执行。 */
  default TaskContainer<C> getArgsChecker() {
    return null;
  }

  /** 数据准备，异步执行。 */
  default AsyncTaskContainer<C> getContextBuilder() {
    return null;
  }

  /** 需同步执行的校验器 */
  default TaskContainer<C> getContextChecker() {
    return null;
  }

  /** 可异步执行的校验器 */
  default AsyncTaskContainer<C> getContextAsyncChecker() {
    return null;
  }
}
