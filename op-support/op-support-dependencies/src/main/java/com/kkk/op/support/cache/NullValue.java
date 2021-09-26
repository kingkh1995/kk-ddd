package com.kkk.op.support.cache;

import com.kkk.op.support.marker.Cache.ValueWrapper;

/**
 * <br>
 *
 * @author KaiKoo
 */
public final class NullValue<T> implements ValueWrapper<T> {

  @SuppressWarnings("rawtypes")
  private static final NullValue INSTANCE = new NullValue<>();

  private NullValue() {}

  @Override
  public T get() {
    return null;
  }

  @SuppressWarnings("unchecked")
  public static <T> ValueWrapper<T> instance() {
    return (ValueWrapper<T>) INSTANCE;
  }
}
