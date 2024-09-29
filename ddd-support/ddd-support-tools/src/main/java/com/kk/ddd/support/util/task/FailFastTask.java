package com.kk.ddd.support.util.task;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

/**
 * <br>
 *
 * @author kingk
 */
@Slf4j
class FailFastTask<C> extends Task<FailFastContext<C>> {

  private final Task<C> task;

  protected FailFastTask(Task<C> task) {
    super(task.name());
    this.task = task;
  }

  @Override
  public Executor executor() {
    return task.executor();
  }

  @Override
  public long timeout() {
    return task.timeout();
  }

  @Override
  protected Consumer<FailFastContext<C>> action() {
    return ffc -> {
      if (ffc.isFailed()) { // fail fast before execute
        log.info("Task[{}] should fail-fast.", task.name());
        return;
      }
      task.action().accept(ffc.getC());
    };
  }

  @Override
  protected Function<FailFastContext<C>, CompletableFuture<Void>> asyncAction() {
    return ffc -> {
      if (ffc.isFailed()) { // fail fast before commit
        log.info("Task[{}] should fail-fast.", task.name());
        return CompletableFuture.completedFuture(null);
      }
      return task.asyncAction().apply(ffc.getC());
    };
  }

  @Override
  protected Consumer<FailFastContext<C>> done() {
    return ffc -> task.done().accept(ffc.getC());
  }

  @Override
  protected BiConsumer<FailFastContext<C>, Throwable> exceptionally() {
    return (ffc, throwable) -> {
      ffc.fail();
      task.exceptionally().accept(ffc.getC(), throwable);
    };
  }
}
