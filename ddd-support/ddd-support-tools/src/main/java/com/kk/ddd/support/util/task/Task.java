package com.kk.ddd.support.util.task;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

/**
 * todo... use VirtualThread <br>
 *
 * @author kingk
 */
@Slf4j
public abstract class Task<C extends TaskContext> implements Function<C, CompletableFuture<Void>> {

  protected final String name;

  protected Task(String name) {
    this.name = name;
  }

  public final String name() {
    return this.name;
  }

  public long timeout() {
    return 10_000L;
  }

  public Executor executor() {
    return ForkJoinPool.commonPool();
  }

  @Override
  public CompletableFuture<Void> apply(C context) {
    if (context.isFailed()) { // fail fast when submit
      log.info("Task[{}] cancel.", name());
      return CompletableFuture.completedFuture(null);
    }
    return asyncAction()
        .apply(context)
        .orTimeout(timeout(), TimeUnit.MILLISECONDS)
        .whenComplete(
            (unused, throwable) -> {
              if (throwable == null) {
                log.info("Task[{}] finish.", name());
              } else { // fall fast if error
                context.failFast();
              }
            });
  }

  protected abstract Consumer<C> action();

  protected Function<C, CompletableFuture<Void>> asyncAction() {
    return context ->
        CompletableFuture.runAsync(
            () -> {
              if (context.isFailed()) { // fall fast when run
                log.info("Task[{}] cancel.", name());
              } else {
                action().accept(context);
              }
            },
            executor());
  }

  static class EmptyTask<C extends TaskContext> extends Task<C> {

    public EmptyTask(String name) {
      super(name);
    }

    @Override
    protected Consumer<C> action() {
      return c -> log.info("this is a empty task [{}].", name());
    }
  }
}
