package com.kkk.op.support.bean;

import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.changeTracking.AbstractAggregateTrackingManager;
import com.kkk.op.support.changeTracking.talsc.ThreadLocalAggregateSnapshotContext;
import com.kkk.op.support.marker.Identifier;
import org.springframework.context.ApplicationContext;

/**
 * 基于ThreadLocal的追踪更新Manager实现类
 *
 * @author KaiKoo
 */
public class ThreadLocalAggregateTrackingManager<T extends Aggregate<ID>, ID extends Identifier> extends
        AbstractAggregateTrackingManager<T, ID> {

    public ThreadLocalAggregateTrackingManager(
            ApplicationContext applicationContext) {
        super(new ThreadLocalAggregateSnapshotContext(applicationContext));
    }
}
