package com.kkk.op.support.marker;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author KaiKoo
 */
public interface DTOAssembler<T extends Entity, V> {

    V toDTO(T t);

    T fromDTO(V dto);

    List<V> toDTO(Collection<T> entityCol);

    List<T> fromDTO(Collection<V> dtoCol);
}
