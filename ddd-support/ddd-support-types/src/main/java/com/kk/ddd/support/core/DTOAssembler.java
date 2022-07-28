package com.kk.ddd.support.core;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DTOAssembler marker <br>
 * todo... fromDTO是否可以删除
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
    return Stream.ofNullable(entityCol)
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .map(this::toDTO)
        .collect(collector);
  }

  default List<T> fromDTO(Collection<? extends V> dtoCol) {
    return this.fromDTO(dtoCol, Collectors.toList());
  }

  default <R> R fromDTO(Collection<? extends V> dtoCol, Collector<? super T, ?, R> collector) {
    return Stream.ofNullable(dtoCol)
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .map(this::fromDTO)
        .collect(collector);
  }
}
