package com.kkk.op.support.changeTracking;

/**
 * 基于ThreadLocal的追踪更新Manager实现类
 *
 * @author KaiKoo
 */
public class ThreadLocalAggregateTrackingManager extends AbstractAggregateTrackingManager {

    public ThreadLocalAggregateTrackingManager() {
        super(new ThreadLocalAggregateSnapshotContext());
    }
}
