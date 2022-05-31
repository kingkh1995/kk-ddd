package com.kk.ddd.support.marker;

import com.kk.ddd.support.base.Aggregate;
import com.kk.ddd.support.tracking.AggregateTrackingManager;

/**
 * 聚合根类Repository
 *
 * @author KaiKoo
 */
public interface AggregateRepository<T extends Aggregate<ID>, ID extends Identifier>
    extends EntityRepository<T, ID> {

  AggregateTrackingManager<T, ID> getAggregateTrackingManager();
}
