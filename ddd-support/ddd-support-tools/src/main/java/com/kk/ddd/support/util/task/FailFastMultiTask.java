package com.kk.ddd.support.util.task;

import com.kk.ddd.support.util.ObjIntTriConsumer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;
import lombok.extern.slf4j.Slf4j;

/**
 * <br>
 *
 * @author kingk
 */
@Slf4j
class FailFastMultiTask<C> extends MultiTask<FailFastContext<C>> {

  private final MultiTask<C> task;

  protected FailFastMultiTask(MultiTask<C> task) {
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
  protected Executor subExecutor() {
    return task.subExecutor();
  }

  @Override
  protected long subTimeout() {
    return task.subTimeout();
  }

  @Override
  protected Consumer<FailFastContext<C>> action() {
    return ffc -> {
      if (ffc.isFailed()) { // fail fast before execute
        log.info("MultiTask[{}] should fail-fast.", task.name());
        return;
      }
      task.action().accept(ffc.getC());
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

  @Override
  protected ToIntFunction<FailFastContext<C>> getCount() {
    return ffc -> task.getCount().applyAsInt(ffc.getC());
  }

  @Override
  protected ObjIntConsumer<FailFastContext<C>> subAction() {
    return (ffc, i) -> {
      if (ffc.isFailed()) { // fail fast before execute
        log.info("MultiTask[{}]({})  should fail-fast.", task.name(), i);
        return;
      }
      task.subAction().accept(ffc.getC(), i);
    };
  }

  @Override
  protected BiFunction<FailFastContext<C>, Integer, CompletableFuture<Void>> asyncSubAction() {
    return (ffc, i) -> {
      if (ffc.isFailed()) { // fail fast before commit
        log.info("Task[{}] should fail-fast.", task.name());
        return CompletableFuture.completedFuture(null);
      }
      return task.asyncSubAction().apply(ffc.getC(), i);
    };
  }

  @Override
  protected ObjIntConsumer<FailFastContext<C>> subDone() {
    return (ffc, i) -> task.subDone().accept(ffc.getC(), i);
  }

  @Override
  protected ObjIntTriConsumer<FailFastContext<C>, Throwable> subExceptionally() {
    return (ffc, throwable, i) -> {
      ffc.fail();
      task.subExceptionally().accept(ffc.getC(), throwable, i);
    };
  }
}
