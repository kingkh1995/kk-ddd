package com.kkk.op.support.marker;

import com.kkk.op.support.base.Entity;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * DTOAssembler marker
 *
 * @author KaiKoo
 */
public interface DTOAssembler<T extends Entity<?>, V> {

  V toDTO(T t);

  T fromDTO(V dto);

  default List<V> toDTO(Collection<? extends T> entityCol) {
    if (entityCol == null || entityCol.isEmpty()) {
      return Collections.EMPTY_LIST;
    }
    return entityCol.stream()
        .filter(Objects::nonNull)
        .map(this::toDTO)
        .collect(Collectors.toList());
  }

  default List<T> fromDTO(Collection<? extends V> dtoCol) {
    if (dtoCol == null || dtoCol.isEmpty()) {
      return Collections.EMPTY_LIST;
    }
    return dtoCol.stream().filter(Objects::nonNull).map(this::fromDTO).collect(Collectors.toList());
  }
}
