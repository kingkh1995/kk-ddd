package com.kkk.op.support.marker;

import com.kkk.op.support.base.Entity;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DataConverter marker接口
 *
 * @author KaiKoo
 */
public interface DataConverter<T extends Entity<?>, P> {

  P toData(T t);

  T fromData(P p);

  default List<P> toData(Collection<? extends T> entityCol) {
    return this.toData(entityCol, Collectors.toList());
  }

  default <R> R toData(Collection<? extends T> entityCol, Collector<? super P, ?, R> collector) {
    return Stream.ofNullable(entityCol)
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .map(this::toData)
        .collect(collector);
  }

  default List<T> fromData(Collection<? extends P> dataCol) {
    return this.fromData(dataCol, Collectors.toList());
  }

  default <R> R fromData(Collection<? extends P> dataCol, Collector<? super T, ?, R> collector) {
    return Stream.ofNullable(dataCol)
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .map(this::fromData)
        .collect(collector);
  }
}
