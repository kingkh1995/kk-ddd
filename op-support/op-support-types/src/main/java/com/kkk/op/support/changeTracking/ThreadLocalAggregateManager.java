package com.kkk.op.support.changeTracking;

import com.kkk.op.support.markers.Aggregate;
import com.kkk.op.support.markers.Identifier;
import javax.validation.constraints.NotNull;

/**
 *
 * @author KaiKoo
 */
public class ThreadLocalAggregateManager <T extends Aggregate<ID>, ID extends Identifier>
        implements AggregateManager<T, ID>{


    @Override
    public void attach(@NotNull T aggregate) {

    }

    @Override
    public void detach(@NotNull T aggregate) {

    }

    @Override
    public void merge(@NotNull T aggregate) {

    }

    @Override
    public T find(ID id) {
        return null;
    }

    @Override
    public EntityDiff detectChanges(T aggregate) {
        return null;
    }
}
