package com.kk.ddd.support.util.task;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Slf4j
public class AsyncTaskContainer<C> implements AsyncContainer<C> {
  @Getter private final String name;

  protected AsyncTaskContainer(final String name) {
    this.name = name;
  }

  @Override
  public TaskResult execute(C context) {
    return execute(context, TaskContainers.ASYNC_TIMEOUT_SECONDS);
  }

  @Override
  public TaskResult execute(C context, int timeout) {
    return TaskResult.succeed();
  }
}
