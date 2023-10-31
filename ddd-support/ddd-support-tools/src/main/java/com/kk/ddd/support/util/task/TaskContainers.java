package com.kk.ddd.support.util.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeoutException;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class TaskContainers {
  public static final int TIMEOUT = 60;
  public static final Executor EXECUTOR = ForkJoinPool.commonPool();

  private TaskContainers() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  private static class EmptyTaskContainer<C> extends TaskContainer<C> {

    protected EmptyTaskContainer(String name) {
      super(name);
    }

    @Override
    public TaskResult execute(C context) {
      return TaskResult.succeed();
    }
  }

  private static class EmptyAsyncTaskContainer<C> extends AsyncTaskContainer<C> {

    protected EmptyAsyncTaskContainer(String name) {
      super(name);
    }

    @Override
    public TaskResult execute(C context, int timeout)
        throws ExecutionException, InterruptedException, TimeoutException {
      return TaskResult.succeed();
    }
  }

  public static <C> TaskContainer<C> empty(final String name) {
    return new EmptyTaskContainer<>(name);
  }

  public static <C> AsyncTaskContainer<C> emptyAsync(final String name) {
    return new EmptyAsyncTaskContainer<>(name);
  }

  public static <C> TaskContainerBuilder<C> newBuilder(final String name) {
    return new TaskContainerBuilder<>(name);
  }
}
