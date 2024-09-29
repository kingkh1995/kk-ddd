package com.kk.ddd.support.util.task;

import com.kk.ddd.support.util.ObjIntTriConsumer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;

/**
 * <br>
 *
 * @author kingk
 */
@Slf4j
public abstract class MultiTask<C> extends Task<C> {

  protected MultiTask(String name) {
    super(name);
  }

  @Override
  protected Consumer<C> action() {
    return context -> log.info("MultiTask[{}] action after all completed.", name());
  }

  @Override
  protected final Function<C, CompletableFuture<Void>> asyncAction() {
    return context ->
        CompletableFuture.allOf(
                IntStream.range(0, getCount().applyAsInt(context))
                    .mapToObj(
                        index ->
                            asyncSubAction()
                                .apply(context, index)
                                .orTimeout(subTimeout(), TimeUnit.MILLISECONDS)
                                .thenApply(
                                    unused -> {
                                      subDone().accept(context, index);
                                      return unused;
                                    })
                                .exceptionallyCompose(
                                    throwable -> {
                                      subExceptionally().accept(context, throwable, index);
                                      return CompletableFuture.failedFuture(throwable);
                                    }))
                    .toArray(CompletableFuture[]::new))
            .thenAcceptAsync(unused -> action().accept(context), executor());
  }

  protected Executor subExecutor() {
    return executor();
  }

  protected long subTimeout() {
    return timeout();
  }

  protected abstract ToIntFunction<C> getCount();

  protected abstract ObjIntConsumer<C> subAction();

  protected BiFunction<C, Integer, CompletableFuture<Void>> asyncSubAction() {
    return (context, index) ->
        CompletableFuture.runAsync(() -> subAction().accept(context, index), subExecutor());
  }

  protected ObjIntConsumer<C> subDone() {
    return (context, index) -> log.info("MultiTask[{}]({}) finish.", name(), index);
  }

  protected ObjIntTriConsumer<C, Throwable> subExceptionally() {
    return (context, throwable, index) ->
        log.error("MultiTask[{}]({}) error.", name(), index, throwable);
  }
}
