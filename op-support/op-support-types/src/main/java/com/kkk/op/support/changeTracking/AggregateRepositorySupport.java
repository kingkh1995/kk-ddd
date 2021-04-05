package com.kkk.op.support.changeTracking;

import com.kkk.op.support.bean.Aggregate;
import com.kkk.op.support.bean.EntityRepositorySupport;
import com.kkk.op.support.changeTracking.diff.EntityDiff;
import com.kkk.op.support.exception.BussinessException;
import com.kkk.op.support.marker.AggregateRepository;
import com.kkk.op.support.marker.CacheManager;
import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.support.marker.Identifier;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * AggregateRepository支持类，通过AggregateTrackingManager实现了追踪更新的功能
 *
 * @author KaiKoo
 */
public abstract class AggregateRepositorySupport<T extends Aggregate<ID>, ID extends Identifier>
        extends EntityRepositorySupport<T, ID> implements AggregateRepository<T, ID> {

    @Getter(AccessLevel.PROTECTED)
    private AggregateTrackingManager<T, ID> aggregateTrackingManager;

    public AggregateRepositorySupport(
            DistributedLock distributedLock,
            CacheManager<T> cacheManager,
            AggregateTrackingManager<T, ID> aggregateTrackingManager) {
        super(distributedLock, cacheManager);
        this.aggregateTrackingManager = Objects.requireNonNull(aggregateTrackingManager);
    }

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
        var aggregate = super.find(id);
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
        super.remove(aggregate);
        // 解除跟踪
        this.detach(aggregate);
    }

    /**
     * EntityRepository 的保存方法实现
     */
    @Override
    public void save(@NotNull T aggregate) {
        // 如果没有 ID，直接插入 不需要获取分布式锁
        if (aggregate.getId() == null) {
            this.onInsert(aggregate);
            // 添加跟踪
            this.attach(aggregate);
            return;
        }
        // update操作
        // 做 diff
        var entityDiff = this.aggregateTrackingManager.detectChanges(aggregate);
        if (entityDiff == null) {
            return;
        }
        if (!this.isAutoCaching()) {
            this.onUpdate(aggregate, entityDiff);
        } else {
            var key = aggregate.getId().getValue();
            if (this.getDistributedLock().tryLock(key)) {
                try {
                    this.getCacheManager().cacheRemove(key);
                    this.onUpdate(aggregate, entityDiff);
                    // todo... 发送消息，延迟双删
                } finally {
                    this.getDistributedLock().unlock(key);
                }
            } else {
                throw new BussinessException("服务繁忙请稍后再试！");
            }
        }
        // 合并跟踪变更
        this.aggregateTrackingManager.merge(aggregate);
    }

    protected abstract void onUpdate(@NotNull T aggregate, @NotNull EntityDiff diff);

    @Override
    protected final void onUpdate(@NotNull T aggregate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> list(@NotEmpty Set<ID> ids) {
        var list = super.list(ids);
        // todo... 一定要attach
        return null;
    }
}
