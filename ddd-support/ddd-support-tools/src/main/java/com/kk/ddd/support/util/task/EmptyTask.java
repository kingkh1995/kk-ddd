package com.kk.ddd.support.util.task;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

/**
 * <br>
 *
 * @author kingk
 */
@Slf4j
class EmptyTask<C> extends Task<C> {

  public EmptyTask(String name) {
    super(name);
  }

  @Override
  public long timeout() {
    return 0L;
  }

  @Override
  protected final Consumer<C> action() {
    return null;
  }

  @Override
  protected final Function<C, CompletableFuture<Void>> asyncAction() {
    return c -> CompletableFuture.completedFuture(null);
  }

  @Override
  protected Consumer<C> done() {
    return c -> log.info("Task[{}] is a empty task.", name());
  }
}
