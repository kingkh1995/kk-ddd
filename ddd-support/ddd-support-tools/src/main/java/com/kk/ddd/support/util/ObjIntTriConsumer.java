package com.kk.ddd.support.util;

/**
 * <br>
 *
 * @see java.util.function.ObjIntConsumer
 * @author kingk
 */
@FunctionalInterface
public interface ObjIntTriConsumer<T, U> {
  void accept(T t, U u, int value);
}
