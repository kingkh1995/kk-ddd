package com.kkk.op.support.changeTracking;

import com.kkk.op.support.bean.Aggregate;
import com.kkk.op.support.marker.Identifier;

/**
 * 基于ThreadLocal的追踪更新Manager实现类
 *
 * @author KaiKoo
 */
public class ThreadLocalAggregateTrackingManager<T extends Aggregate<ID>, ID extends Identifier> extends
        AbstractAggregateTrackingManager<T, ID> {

    public ThreadLocalAggregateTrackingManager() {
        super(new ThreadLocalAggregateSnapshotContext());
    }
}
