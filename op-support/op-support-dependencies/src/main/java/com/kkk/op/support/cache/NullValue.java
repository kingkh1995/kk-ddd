package com.kkk.op.support.cache;

import com.kkk.op.support.marker.Cache.ValueWrapper;

/**
 * <br>
 *
 * @author KaiKoo
 */
public final class NullValue<T> implements ValueWrapper<T> {

  public static final NullValue INSTANCE = new NullValue<>();

  private NullValue() {}

  @Override
  public T get() {
    return null;
  }
}
