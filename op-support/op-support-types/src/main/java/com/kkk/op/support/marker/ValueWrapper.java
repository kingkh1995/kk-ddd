package com.kkk.op.support.marker;

import com.kkk.op.support.marker.ValueWrapper.NullValue;
import com.kkk.op.support.marker.ValueWrapper.SimpleValue;
import javax.validation.constraints.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * <br>
 *
 * @author KaiKoo
 */
public sealed interface ValueWrapper<T> permits NullValue, SimpleValue {

  @Nullable
  T get();

  boolean isNullValue();

  final class NullValue<T> implements ValueWrapper<T> {

    public static final NullValue INSTANCE = new NullValue<>();

    private NullValue() {}

    @Override
    public T get() {
      return null;
    }

    @Override
    public boolean isNullValue() {
      return true;
    }
  }

  final class SimpleValue<T> implements ValueWrapper {

    @NotNull private final T value;

    public SimpleValue(@NotNull T value) {
      Assert.notNull(value,"Value shouldn't be null!");
      this.value = value;
    }

    @Override
    public T get() {
      return this.value;
    }

    @Override
    public boolean isNullValue() {
      return false;
    }
  }
}
