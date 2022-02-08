package com.kkk.op.support.marker;

import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.tracking.AggregateTrackingManager;

/**
 * 聚合根类Repository
 *
 * @author KaiKoo
 */
public interface AggregateRepository<T extends Aggregate<ID>, ID extends Identifier>
    extends EntityRepository<T, ID> {

  AggregateTrackingManager<T, ID> getAggregateTrackingManager();
}
