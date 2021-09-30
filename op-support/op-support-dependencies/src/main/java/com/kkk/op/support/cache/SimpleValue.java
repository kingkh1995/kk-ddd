package com.kkk.op.support.cache;

import com.kkk.op.support.marker.Cache.ValueWrapper;
import javax.validation.constraints.NotNull;
import org.springframework.util.Assert;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class SimpleValue<T> implements ValueWrapper<T> {

  @NotNull private final T value;

  private SimpleValue(T value) {
    this.value = value;
  }

  @Override
  public T get() {
    return this.value;
  }

  public static <T> ValueWrapper<T> from(@NotNull T value) {
    Assert.notNull(value, "Value shouldn't be null!");
    return new SimpleValue<>(value);
  }
}
