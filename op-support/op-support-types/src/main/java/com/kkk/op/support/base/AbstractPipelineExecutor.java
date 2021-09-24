package com.kkk.op.support.base;

import com.alibaba.ttl.TtlWrappers;
import com.kkk.op.support.marker.PipelineHandler;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 管道模式执行器基类，支持同步和异步 <br>
 *
 * @author KaiKoo
 */
public abstract class AbstractPipelineExecutor<C, K, H extends PipelineHandler<C, K>>
    extends AbstractStrategyManager<K, H> {

  public AbstractPipelineExecutor() {
    super(null);
  }

  @Override
  protected abstract Comparator<K> getKComparator();

  protected void handleBefore(C context) {}

  protected void handleAfter(C context) {}

  // 同步执行管道
  public boolean acceptSync(C context) {
    handleBefore(Objects.requireNonNull(context, "Context shouldn't be null!"));
    var lastSuccess = true;
    for (var handler : super.getAll()) {
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
    return CompletableFuture.supplyAsync(TtlWrappers.wrap(() -> acceptSync(context)));
  }

  // 异步执行管道
  public CompletableFuture<Boolean> acceptAsync(C context, Executor executor) {
    return CompletableFuture.supplyAsync(TtlWrappers.wrap(() -> acceptSync(context)), executor);
  }
}
