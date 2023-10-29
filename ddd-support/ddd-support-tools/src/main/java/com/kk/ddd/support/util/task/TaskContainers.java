package com.kk.ddd.support.util.task;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class TaskContainers {
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
    public TaskResult execute(C context, int timeout) {
      return TaskResult.succeed();
    }
  }

  public static <C> TaskContainer<C> empty(final String name) {
    return new EmptyTaskContainer<>(name);
  }

  public static <C> AsyncTaskContainer<C> emptyAsync(final String name) {
    return new EmptyAsyncTaskContainer<>(name);
  }

  public static <C> TaskContainer.Builder<C> newBuilder(final String name) {
    return new TaskContainer.Builder<>(name);
  }

  public static <C> AsyncTaskContainer.Builder<C> newAsyncBuilder(final String name) {
    return new AsyncTaskContainer.Builder<>(name);
  }
}
