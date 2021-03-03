package com.kkk.op.support.changeTracking;

import com.kkk.op.support.markers.Aggregate;
import com.kkk.op.support.markers.AggregateRepository;
import com.kkk.op.support.markers.Identifier;
import lombok.AccessLevel;
import lombok.Getter;

/**
 *
 * @author KaiKoo
 */
public abstract class AggregateRepositorySupport<T extends Aggregate<ID>, ID extends
        Identifier> implements AggregateRepository<T, ID> {

    private final Class<T> targetClass;
    @Getter(AccessLevel.PROTECTED)
    private AggregateManager<T, ID> aggregateManager;

    public AggregateRepositorySupport(Class<T> targetClass,
            AggregateManager<T, ID> aggregateManager) {
        this.targetClass = targetClass;
        this.aggregateManager = aggregateManager;
    }
}
