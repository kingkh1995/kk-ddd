package com.kkk.op.support.changeTracking;

import com.kkk.op.support.changeTracking.diff.DiffUtil;
import com.kkk.op.support.changeTracking.diff.EntityDiff;
import com.kkk.op.support.markers.Aggregate;
import com.kkk.op.support.markers.Identifier;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;

/**
 * Aggregate追踪快照管理的真正的实现类，实现其他接口以拓展更多功能
 * @author KaiKoo
 */
public class AggregateTrackingContext<T extends Aggregate<ID>, ID extends Identifier> implements
        AggregateTrackingManager<T, ID> {

    private final Map<ID, T> map = new HashMap<>();

    public AggregateTrackingContext() {
    }

    @Override
    public void attach(@NotNull T aggregate) {
        if (aggregate.getId() != null && !this.map.containsKey(aggregate.getId())) {
            // 借助merge
            this.merge(aggregate);
        }
    }

    @Override
    public void detach(@NotNull T aggregate) {
        if (aggregate.getId() != null) {
            this.map.remove(aggregate.getId());
        }
    }

    @Override
    public void merge(@NotNull T aggregate) {
        if (aggregate.getId() != null) {
            // 获取快照
            T snapshot = (T) aggregate.snapshot();
            this.map.put(aggregate.getId(), snapshot);
        }
    }

    @Override
    public T find(@NotNull ID id) {
        return this.map.get(id);
    }

    @Override
    public EntityDiff detectChanges(@NotNull T aggregate) {
        if (aggregate.getId() == null) {
            return EntityDiff.EMPTY;
        }
        T snapshot = this.map.get(aggregate.getId());
        // 如果不存在快照，保存
        if (snapshot == null) {
            attach(aggregate);
        }
        return DiffUtil.diff(snapshot, aggregate);
    }
}
