package com.kk.ddd.support.core;

/**
 * 聚合根类Repository
 *
 * @author KaiKoo
 */
public interface AggregateRepository<T extends Aggregate<ID>, ID extends Identifier>
    extends EntityRepository<T, ID> {

  AggregateTrackingManager<T, ID> getAggregateTrackingManager();
}
