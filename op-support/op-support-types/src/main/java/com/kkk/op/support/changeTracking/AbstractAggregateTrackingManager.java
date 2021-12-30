package com.kkk.op.support.changeTracking;

import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.changeTracking.diff.Diff;
import com.kkk.op.support.changeTracking.diff.DiffUtil;
import com.kkk.op.support.marker.Identifier;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 * 对外提供追踪变更的功能，内部定义好追踪管理的方式，快照的管理交由AggregateSnapshotContext去实现 <br>
 * 参考CacheManager设计，可以增加其他的接口实现来拓展功能 <br>
 * 参考TtlCopier，增加Snapshooter接口实现快照拍摄功能
 *
 * @author KaiKoo
 */
public abstract class AbstractAggregateTrackingManager<
        T extends Aggregate<ID>, ID extends Identifier>
    implements AggregateTrackingManager<T, ID>, Snapshooter<T> {

  private final AggregateSnapshotContext<T, ID> context;

  public AbstractAggregateTrackingManager(AggregateSnapshotContext<T, ID> context) {
    this.context = Objects.requireNonNull(context);
  }

  @Override
  public void attach(@NotNull T aggregate) {
    if (aggregate.isIdentified()) {
      // 生成一份快照并保存
      this.context.putSnapshot(this.snapshoot(aggregate));
    }
  }

  @Override
  public void detach(@NotNull T aggregate) {
    if (aggregate.isIdentified()) {
      this.context.removeSnapshot(aggregate.getId());
    }
  }

  @Override
  public Diff detectChanges(@NotNull T aggregate) {
    // 生成快照进行对比，因为属性可能被Diff持有，有被修改的风险
    return DiffUtil.diff(this.find(aggregate.getId()), aggregate);
  }

  @Override
  public boolean exist(@NotNull ID id) {
    return this.context.existSnapshot(id);
  }

  @Override
  public T find(@NotNull ID id) {
    // 使用snapshoot方法将快照复制一份返回
    return this.exist(id) ? this.snapshoot(this.context.getSnapshot(id)) : null;
  }
}
