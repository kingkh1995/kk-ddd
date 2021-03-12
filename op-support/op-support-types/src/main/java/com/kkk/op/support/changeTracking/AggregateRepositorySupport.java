package com.kkk.op.support.changeTracking;

import com.kkk.op.support.changeTracking.diff.EntityDiff;
import com.kkk.op.support.marker.Aggregate;
import com.kkk.op.support.marker.AggregateRepository;
import com.kkk.op.support.marker.Identifier;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * AggregateRepository支持类，通过AggregateTrackingManager实现了追踪更新的功能
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
    protected abstract ID onInsert(@NotNull T aggregate);

    protected abstract T onSelect(@NotNull ID id);

    protected abstract void onUpdate(@NotNull T aggregate, @NotNull EntityDiff diff);

    protected abstract void onDelete(@NotNull T aggregate);

    /**
     * 让查询出来的对象能够被追踪。
     * 如果自己实现了一个定制查询接口，要记得单独调用 attach。
     */
    @Override
    public void attach(@NotNull T aggregate) {
        this.aggregateTrackingManager.attach(aggregate);
    }

    /**
     * 停止追踪。
     * 如果自己实现了一个定制移除接口，要记得单独调用 detach。
     */
    @Override
    public void detach(@NotNull T aggregate) {
        this.aggregateTrackingManager.detach(aggregate);
    }

    /**
     * EntityRepository 的查询方法实现
     */
    @Override
    public T find(@NotNull ID id) {
        var aggregate = this.onSelect(id);
        // 添加跟踪
        if (aggregate != null) {
            this.attach(aggregate);
        }
        return aggregate;
    }

    /**
     * EntityRepository 的移除方法实现
     */
    @Override
    public void remove(@NotNull T aggregate) {
        this.onDelete(aggregate);
        // 解除跟踪
        this.detach(aggregate);
    }

    /**
     * EntityRepository 的保存方法实现
     */
    @Override
    public ID save(@NotNull T aggregate) {
        // 如果没有 ID，直接插入
        if (aggregate.getId() == null) {
            var id = this.onInsert(aggregate);
            // 添加跟踪
            this.attach(aggregate);
            return id;
        }
        // 做 diff
        var entityDiff = aggregateTrackingManager.detectChanges(aggregate);
        if (entityDiff != null) {
            // 调用 UPDATE
            this.onUpdate(aggregate, entityDiff);
            // 合并变更跟踪
            aggregateTrackingManager.merge(aggregate);
        }
        return aggregate.getId();
    }
}
