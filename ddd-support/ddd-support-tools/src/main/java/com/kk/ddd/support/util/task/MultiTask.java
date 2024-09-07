package com.kk.ddd.support.util.task;

import java.util.concurrent.CompletableFuture;
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

  protected abstract ToIntFunction<C> getCount();

  protected abstract ObjIntConsumer<C> subAction();

  @Override
  protected final Consumer<C> action() {
    return null;
  }

  @Override
  protected final Function<C, CompletableFuture<Void>> asyncAction() {
    return context ->
        CompletableFuture.allOf(
            IntStream.range(0, getCount().applyAsInt(context))
                .mapToObj(
                    index ->
                        CompletableFuture.runAsync(
                                () -> subAction().accept(context, index), executor())
                            .whenComplete(
                                (unused, throwable) ->
                                    whenSubCompleteAction(context, index, throwable)))
                .toArray(CompletableFuture[]::new));
  }

  protected void whenSubCompleteAction(C context, int index, Throwable throwable) {
    if (throwable == null) {
      log.info("MultiTask[{}]({}) finish.", name(), index);
    } else {
      log.error("MultiTask[{}]({}) error.", name(), index, throwable);
    }
  }
}
