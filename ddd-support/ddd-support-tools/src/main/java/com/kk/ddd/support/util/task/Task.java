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
public abstract class Task<C> implements Function<C, CompletableFuture<Void>> {

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
  public final CompletableFuture<Void> apply(C context) {
    return asyncAction()
        .apply(context)
        .orTimeout(timeout(), TimeUnit.MILLISECONDS)
        .whenCompleteAsync(
            (unused, throwable) -> {
              whenCompleteAction(context, throwable);
            },
            executor());
  }

  protected abstract Consumer<C> action();

  protected Function<C, CompletableFuture<Void>> asyncAction() {
    return context -> CompletableFuture.runAsync(() -> action().accept(context), executor());
  }

  protected void whenCompleteAction(C context, Throwable throwable) {
    if (throwable == null) {
      log.info("Task[{}] finish.", name());
    } else {
      log.error("Task[{}] error.", name(), throwable);
    }
  }

  static class EmptyTask<C> extends Task<C> {

    public EmptyTask(String name) {
      super(name);
    }

    @Override
    protected Consumer<C> action() {
      return c -> log.info("this is a empty action.");
    }
  }
}
