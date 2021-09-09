package com.kkk.op.support.bean;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.changeTracking.AbstractAggregateTrackingManager;
import com.kkk.op.support.changeTracking.AggregateSnapshotContext;
import com.kkk.op.support.marker.Identifier;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;

/**
 * 基于ThreadLocal的追踪更新Manager实现类
 *
 * @author KaiKoo
 */
public class ThreadLocalAggregateTrackingManager<T extends Aggregate<ID>, ID extends Identifier>
    extends AbstractAggregateTrackingManager<T, ID> {

  public ThreadLocalAggregateTrackingManager() {
    super(new ThreadLocalAggregateSnapshotContext<>());
  }

  /**
   * Aggregate快照管理实现类 使用ThreadLocal防止多个线程公用一份快照 <br>
   * 内存泄漏解决方案： <br>
   * 1.putSnapshot 方法将 ThreadLocal 记录到 Recorder <br>
   * 2.在拦截器的 afterCompletion 方法中移除所有的 ThreadLocal <br>
   * <br>
   *
   * @author KaiKoo
   */
  protected static class ThreadLocalAggregateSnapshotContext<
          T extends Aggregate<ID>, ID extends Identifier>
      implements AggregateSnapshotContext<T, ID> {

    // 使用TransmittableThreadLocal
    private final ThreadLocal<Map<ID, T>> threadLocal =
        new TransmittableThreadLocal<>() {
          @Override
          protected Map<ID, T> initialValue() {
            return new HashMap<>();
          }
        };

    @Override
    public boolean existSnapshot(@NotNull ID id) {
      return this.threadLocal.get().containsKey(id);
    }

    @Override
    public T removeSnapshot(@NotNull ID id) {
      return this.threadLocal.get().remove(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void putSnapshot(@NotNull T aggregate) {
      // 获取快照
      if (aggregate.getId() != null) {
        var snapshot = (T) aggregate.snapshot();
        this.threadLocal.get().put(snapshot.getId(), snapshot);
        // 记录到 Recorder
        ThreadLocalRecorder.recordTlasc(this.threadLocal);
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getSnapshot(@NotNull ID id) {
      // 返回副本
      return (T) this.threadLocal.get().get(id).snapshot();
    }
  }
}
