package com.kkk.op.support.base;

import com.alibaba.ttl.TtlWrappers;
import com.kkk.op.support.marker.PipelineHandler;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 管道模式执行器基类，支持同步和异步 <br>
 * 管道模式和责任链模式的区别： <br>
 * 管道模式：数据沿着设定的管道流转，所有管道均会处理数据。 <br>
 * 责任链模式：数据沿着设定的链条向上委托直到能被处理，故数据只会被成功处理一次。 <br>
 *
 * @author KaiKoo
 */
public abstract class AbstractPipelineExecutor<C, K, H extends PipelineHandler<C, K>>
    extends ApplicationContextAwareSingleton {
  private SortedSet<H> handlers;

  @Override
  public void afterSingletonsInstantiated() {
    if (this.handlers != null) {
      return;
    }
    // 使用KComparator收集为TreeSet，再封装为UnmodifiableSortedSet
    this.handlers =
        this.getApplicationContext().getBeansOfType(this.getHClass()).values().stream()
            .collect(
                Collectors.collectingAndThen(
                    Collectors.toCollection(
                        () ->
                            new TreeSet<>(
                                Comparator.comparing(
                                    PipelineHandler::getIdentifier, this.getKComparator()))),
                    Collections::unmodifiableSortedSet));
  }

  protected abstract Class<H> getHClass();

  protected abstract Comparator<K> getKComparator();

  protected void handleBefore(C context) {}

  protected void handleAfter(C context) {}

  protected SortedSet<H> getPipe() {
    return this.handlers;
  }

  // 同步执行管道
  public boolean acceptSync(C context) {
    handleBefore(Objects.requireNonNull(context, "Context shouldn't be null!"));
    var lastSuccess = true;
    for (var handler : this.getPipe()) {
      lastSuccess = handler.handle(context);
      if (!lastSuccess) {
        break;
      }
    }
    handleAfter(context);
    return lastSuccess;
  }

  // 异步执行管道
  public CompletableFuture<Boolean> acceptAsync(C context) {
    return CompletableFuture.supplyAsync(TtlWrappers.wrapSupplier(() -> acceptSync(context)));
  }

  // 异步执行管道
  public CompletableFuture<Boolean> acceptAsync(C context, Executor executor) {
    return CompletableFuture.supplyAsync(
        TtlWrappers.wrapSupplier(() -> acceptSync(context)), executor);
  }
}
