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
 * DataConverter marker接口
 *
 * @author KaiKoo
 */
public interface DataConverter<T extends Entity<?>, M> {

  M toData(T t);

  T fromData(M m);

  default List<M> toData(Collection<? extends T> entityCol) {
    return this.toData(entityCol, Collectors.toList());
  }

  default <R> R toData(Collection<? extends T> entityCol, Collector<? super M, ?, R> collector) {
    return Optional.ofNullable(entityCol)
        .map(Collection::stream)
        .orElse(Stream.empty())
        .filter(Objects::nonNull)
        .map(this::toData)
        .collect(collector);
  }

  default List<T> fromData(Collection<? extends M> dataCol) {
    return this.fromData(dataCol, Collectors.toList());
  }

  default <R> R fromData(Collection<? extends M> dataCol, Collector<? super T, ?, R> collector) {
    return Optional.ofNullable(dataCol)
        .map(Collection::stream)
        .orElse(Stream.empty())
        .filter(Objects::nonNull)
        .map(this::fromData)
        .collect(collector);
  }
}
