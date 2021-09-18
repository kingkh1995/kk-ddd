package com.kkk.op.support.marker;

import com.kkk.op.support.base.Entity;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DTOAssembler marker
 *
 * @author KaiKoo
 */
public interface DTOAssembler<T extends Entity<?>, V> {

  V toDTO(T t);

  T fromDTO(V v);

  default List<V> toDTO(Collection<? extends T> entityCol) {
    return this.toDTO(entityCol, Collectors.toList());
  }

  default <R> R toDTO(Collection<? extends T> entityCol, Collector<? super V, ?, R> collector) {
    return Optional.ofNullable(entityCol)
        .map(Collection::stream)
        .orElse(Stream.empty())
        .filter(Objects::nonNull)
        .map(this::toDTO)
        .collect(collector);
  }

  default List<T> fromDTO(Collection<? extends V> dtoCol) {
    return this.fromDTO(dtoCol, Collectors.toList());
  }

  default <R> R fromDTO(Collection<? extends V> dtoCol, Collector<? super T, ?, R> collector) {
    return Optional.ofNullable(dtoCol)
        .map(Collection::stream)
        .orElse(Stream.empty())
        .filter(Objects::nonNull)
        .map(this::fromDTO)
        .collect(collector);
  }
}
