package com.kkk.op.support.changeTracking;

import com.kkk.op.support.markers.Aggregate;
import com.kkk.op.support.markers.AggregateRepository;
import com.kkk.op.support.markers.Identifier;
import lombok.Getter;

/**
 *
 * @author KaiKoo
 */
public abstract class AggregateRepositorySupport<T extends Aggregate<ID>, ID extends
        Identifier> implements AggregateRepository<T, ID> {

    @Getter
    private final Class<T> targetClass;

    protected AggregateRepositorySupport(Class<T> targetClass) {
        this.targetClass = targetClass;
    }
}
