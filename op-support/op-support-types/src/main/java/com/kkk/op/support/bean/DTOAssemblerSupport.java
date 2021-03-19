package com.kkk.op.support.bean;

import com.kkk.op.support.marker.DTOAssembler;
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
public abstract class DTOAssemblerSupport<T extends Entity, V> implements DTOAssembler<T, V> {

    protected abstract V buildDTOFromEntity(@NotNull T t);

    protected abstract T buildEntityFromDTO(@NotNull V dto);

    @Override
    public V toDTO(T t) {
        if (t == null) {
            return null;
        }
        return buildDTOFromEntity(t);
    }

    @Override
    public T fromDTO(V dto) {
        if (dto == null) {
            return null;
        }
        return this.buildEntityFromDTO(dto);
    }

    @Override
    public List<V> toDTO(Collection<T> entityCol) {
        if (entityCol == null || entityCol.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return entityCol.stream().map(this::toDTO).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> fromDTO(Collection<V> dtoCol) {
        if (dtoCol == null || dtoCol.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return dtoCol.stream().map(this::fromDTO).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
