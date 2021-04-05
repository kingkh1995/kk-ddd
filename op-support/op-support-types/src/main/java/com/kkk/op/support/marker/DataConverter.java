package com.kkk.op.support.marker;

import com.kkk.op.support.bean.Entity;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * DataConverter marker接口
 * @author KaiKoo
 */
public interface DataConverter<T extends Entity, P> {

    P toData(T t);

    T fromData(P data);

    default List<P> toData(Collection<? extends T> entityCol) {
        if (entityCol == null || entityCol.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return entityCol.stream().filter(Objects::nonNull).map(this::toData)
                .collect(Collectors.toList());
    }

    default List<T> fromData(Collection<? extends P> dataCol) {
        if (dataCol == null || dataCol.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return dataCol.stream().filter(Objects::nonNull).map(this::fromData)
                .collect(Collectors.toList());
    }

}
