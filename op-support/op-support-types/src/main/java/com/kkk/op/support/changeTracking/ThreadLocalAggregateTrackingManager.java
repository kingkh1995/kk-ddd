package com.kkk.op.support.changeTracking;

import com.kkk.op.support.changeTracking.diff.EntityDiff;
import com.kkk.op.support.markers.Aggregate;
import com.kkk.op.support.markers.Identifier;
import javax.validation.constraints.NotNull;

/**
 * 使用 ThreadLocal 避免多线程公用同一个 Entity 的情况
 * 门面模式 实则由AggregateTrackingContext 管理 Aggregate 快照
 * @author KaiKoo
 */
public class ThreadLocalAggregateTrackingManager<T extends Aggregate<ID>, ID extends Identifier>
        implements AggregateTrackingManager<T, ID> {

    private final ThreadLocal<AggregateTrackingContext<T, ID>> context;

    public ThreadLocalAggregateTrackingManager() {
        this.context = ThreadLocal.withInitial(AggregateTrackingContext::new);
    }

    @Override
    public void attach(@NotNull T aggregate) {
        this.context.get().attach(aggregate);
    }

    @Override
    public void detach(@NotNull T aggregate) {
        this.context.get().detach(aggregate);
    }

    @Override
    public void merge(@NotNull T aggregate) {
        this.context.get().merge(aggregate);
    }

    @Override
    public T find(@NotNull ID id) {
        return this.context.get().find(id);
    }

    @Override
    public EntityDiff detectChanges(@NotNull T aggregate) {
        return this.context.get().detectChanges(aggregate);
    }

}
