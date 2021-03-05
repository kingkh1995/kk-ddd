package com.kkk.op.support.changeTracking;

/**
 *
 * 基于ThreadLocal的追踪更新管理类
 * @author KaiKoo
 */
public class ThreadLocalAggregateTrackingManager extends AbstractAggregateTrackingManager {

    public ThreadLocalAggregateTrackingManager() {
        super(new ThreadLocalAggregateSnapshotContext());
    }
}
