package com.kkk.op.support.bean;

import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.changeTracking.AbstractAggregateTrackingManager;
import com.kkk.op.support.changeTracking.AggregateSnapshotContext;
import com.kkk.op.support.changeTracking.Snapshooter;
import com.kkk.op.support.marker.Identifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import lombok.Builder;

/**
 * 基于ThreadLocal的追踪更新Manager实现类 <br>
 *
 * @author KaiKoo
 */
@Builder
public class ThreadLocalAggregateTrackingManager<T extends Aggregate<ID>, ID extends Identifier>
    extends AbstractAggregateTrackingManager<T, ID> {

  private final Snapshooter<T> snapshooter;

  private ThreadLocalAggregateTrackingManager(Snapshooter<T> snapshooter) {
    super(new ThreadLocalAggregateSnapshotContext<>());
    this.snapshooter = Objects.requireNonNull(snapshooter);
  }

  @Override
  public T snapshoot(T aggregate) {
    return this.snapshooter.snapshoot(aggregate);
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

    // todo... 待定
    private final ThreadLocal<Map<ID, T>> threadLocal = ThreadLocal.withInitial(HashMap::new);

    @Override
    public boolean existSnapshot(@NotNull ID id) {
      return this.threadLocal.get().containsKey(id);
    }

    @Override
    public T removeSnapshot(@NotNull ID id) {
      return this.threadLocal.get().remove(id);
    }

    @Override
    public void putSnapshot(@NotNull T snapshot) {
      if (snapshot.isIdentified()) {
        this.threadLocal.get().put(snapshot.getId(), snapshot);
        // 记录到 Recorder
        ThreadLocalRecorder.recordTlasc(this.threadLocal);
      }
    }

    @Override
    public T getSnapshot(@NotNull ID id) {
      return this.threadLocal.get().get(id);
    }
  }
}
