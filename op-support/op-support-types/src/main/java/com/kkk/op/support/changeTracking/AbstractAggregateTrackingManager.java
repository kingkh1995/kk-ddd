package com.kkk.op.support.changeTracking;

import com.kkk.op.support.changeTracking.diff.DiffUtil;
import com.kkk.op.support.changeTracking.diff.EntityDiff;
import com.kkk.op.support.marker.Aggregate;
import com.kkk.op.support.marker.Identifier;
import javax.validation.constraints.NotNull;

/**
 *
 * 参考CacheManager设计
 * 对外提供追踪变更的功能，内部定义好追踪管理的方式，快照的操作交由AggregateSnapshotContext去实现
 * 增加其他的接口实现已拓展其他功能
 * @author KaiKoo
 */
public abstract class AbstractAggregateTrackingManager<T extends Aggregate<ID>, ID extends Identifier>
        implements AggregateTrackingManager<T, ID> {

    private final AggregateSnapshotContext<T, ID> context;

    public AbstractAggregateTrackingManager(
            AggregateSnapshotContext context) {
        this.context = context;
    }

    @Override
    public void attach(@NotNull T aggregate) {
        if (aggregate.getId() != null && !this.context.existSnapshot(aggregate.getId())) {
            // 借助merge
            this.merge(aggregate);
        }
    }

    @Override
    public void detach(@NotNull T aggregate) {
        if (aggregate.getId() != null) {
            this.context.removeSnapshot(aggregate.getId());
        }
    }

    @Override
    public void merge(@NotNull T aggregate) {
        if (aggregate.getId() != null) {
            this.context.putSnapshot(aggregate);
        }
    }

    @Override
    public EntityDiff detectChanges(@NotNull T aggregate) {
        if (aggregate.getId() != null) {
            return DiffUtil.diff(this.find(aggregate.getId()), aggregate);
        }
        return EntityDiff.EMPTY;
    }

    @Override
    public boolean exist(@NotNull ID id) {
        return this.context.existSnapshot(id);
    }

    @Override
    public T find(@NotNull ID id) {
        return this.context.getSnapshot(id);
    }
}
