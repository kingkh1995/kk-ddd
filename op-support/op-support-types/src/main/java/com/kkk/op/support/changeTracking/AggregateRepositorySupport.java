package com.kkk.op.support.changeTracking;

import com.kkk.op.support.changeTracking.diff.EntityDiff;
import com.kkk.op.support.markers.Aggregate;
import com.kkk.op.support.markers.AggregateRepository;
import com.kkk.op.support.markers.Identifier;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;

/**
 *
 * @author KaiKoo
 */
public abstract class AggregateRepositorySupport<T extends Aggregate<ID>, ID extends
        Identifier> implements AggregateRepository<T, ID> {

    @Getter(AccessLevel.PROTECTED)
    private AggregateTrackingManager<T, ID> aggregateTrackingManager;

    public AggregateRepositorySupport(AggregateTrackingManager<T, ID> aggregateTrackingManager) {
        this.aggregateTrackingManager = aggregateTrackingManager;
    }

    /**
     * 这几个方法是继承的子类应该去实现的 对应crud的实现
     */
    protected abstract ID onInsert(T aggregate);

    protected abstract T onSelect(ID id);

    protected abstract void onUpdate(T aggregate, EntityDiff diff);

    protected abstract void onDelete(T aggregate);

    @Override
    public void attach(@NotNull T aggregate) {
        this.aggregateTrackingManager.attach(aggregate);
    }

    @Override
    public void detach(@NotNull T aggregate) {
        this.aggregateTrackingManager.detach(aggregate);
    }

    /**
     * 让查询出来的对象能够被追踪。
     * 如果自己实现了一个定制查询接口，要记得单独调用 attach。
     */
    @Override
    public T find(@NotNull ID id) {
        T aggregate = this.onSelect(id);
        if (aggregate != null) {
            this.attach(aggregate);
        }
        return aggregate;
    }

    /**
     * 停止追踪。
     * 如果自己实现了一个定制移除接口，要记得单独调用 detach。
     */
    @Override
    public void remove(@NotNull T aggregate) {
        this.onDelete(aggregate);
        this.detach(aggregate);
    }

    @Override
    public ID save(@NotNull T aggregate) {
        // 如果没有 ID，直接插入
        if (aggregate.getId() == null) {
            ID id = this.onInsert(aggregate);
            this.attach(aggregate);
            return id;
        }
        // 做 Diff
        EntityDiff diff = aggregateTrackingManager.detectChanges(aggregate);
        if (!diff.isEmpty()) {
            // 调用 UPDATE
            this.onUpdate(aggregate, diff);
            // 最终将 DB 带来的变化更新回 AggregateManager
            aggregateTrackingManager.merge(aggregate);
        }
        return aggregate.getId();
    }
}
