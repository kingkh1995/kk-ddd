package com.kkk.op.support.bean;

import com.kkk.op.support.marker.DataConverter;
import com.kkk.op.support.marker.Entity;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

/**
 *
 * @author KaiKoo
 */
public abstract class DataConvertSupport<T extends Entity, P> implements DataConverter<T, P> {

    protected abstract P buildDataFromEntity(@NotNull T t);

    protected abstract T buildEntityFromData(@NotNull P data);

    @Override
    public P toData(T t) {
        if (t == null) {
            return null;
        }
        return this.buildDataFromEntity(t);
    }

    @Override
    public T fromData(P data) {
        if (data == null) {
            return null;
        }
        return this.buildEntityFromData(data);
    }

    @Override
    public List<P> toData(Collection<T> entityCol) {
        if (entityCol == null || entityCol.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return entityCol.stream().map(this::toData).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> fromData(Collection<P> dataCol) {
        if (dataCol == null || dataCol.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return dataCol.stream().map(this::fromData).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
