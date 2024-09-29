package com.kk.ddd.support.util.task;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
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

  public Executor executor() {
    return ForkJoinPool.commonPool();
  }

  public abstract long timeout();

  @Override
  public final CompletableFuture<Void> apply(C context) {
    return asyncAction()
        .apply(context)
        .orTimeout(timeout(), TimeUnit.MILLISECONDS)
        .thenApply(
            unused -> {
              done().accept(context);
              return unused;
            })
        .exceptionallyCompose(
            throwable -> {
              exceptionally().accept(context, throwable);
              return CompletableFuture.failedFuture(throwable);
            });
  }

  protected abstract Consumer<C> action();

  protected Function<C, CompletableFuture<Void>> asyncAction() {
    return context -> CompletableFuture.runAsync(() -> action().accept(context), executor());
  }

  protected Consumer<C> done() {
    return context -> log.info("Task[{}] finish.", name());
  }

  protected BiConsumer<C, Throwable> exceptionally() {
    return (context, throwable) -> log.error("Task[{}] error.", name(), throwable);
  }
}
