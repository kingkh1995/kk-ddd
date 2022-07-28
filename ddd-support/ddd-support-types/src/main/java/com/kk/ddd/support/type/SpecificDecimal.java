package com.kk.ddd.support.type;

import com.fasterxml.jackson.annotation.JsonValue;
import com.kk.ddd.support.core.Type;
import com.kk.ddd.support.util.IllegalArgumentExceptions;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * 特定数字DP类基类 <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true)
public abstract class SpecificDecimal extends Number implements Type {

  private final BigDecimal value;

  /**
   * @param value 数值
   * @param fieldName 字段名称
   * @param min 最小值 Nullable
   * @param minInclusive 是否包含最小值
   * @param max 最大值 Nullable
   * @param maxInclusive 是否包含最大值
   * @param scale 小数位
   */
  protected SpecificDecimal(
      @NotNull BigDecimal value,
      String fieldName,
      BigDecimal min,
      Boolean minInclusive,
      BigDecimal max,
      Boolean maxInclusive,
      Integer scale) {
    if (min != null) {
      var cmp = value.compareTo(min);
      if ((minInclusive && cmp < 0) || (!minInclusive && cmp <= 0)) {
        throw IllegalArgumentExceptions.forMinValue(fieldName, min, minInclusive);
      }
    }
    if (max != null) {
      var cmp = value.compareTo(max);
      if ((maxInclusive && cmp > 0) || (!maxInclusive && cmp >= 0)) {
        throw IllegalArgumentExceptions.forMaxValue(fieldName, max, maxInclusive);
      }
    }
    if (scale != null) {
      // 先去掉尾部的0 并可能会被转为科学计数法表示
      value = value.stripTrailingZeros();
      if (value.scale() > scale) {
        throw IllegalArgumentExceptions.forScaleAbove(fieldName, scale);
      }
      value = value.setScale(scale); // 最后设置scale补全后面的0 同时保证scale非负时为原生数字表示 负数则为科学计数法表示
    }
    this.value = Objects.requireNonNull(value);
  }

  protected static BigDecimal parseBigDecimal(Object o, String fieldName) {
    if (o == null) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    } else if (o instanceof BigDecimal v) {
      return v;
    } else if (o instanceof BigInteger v) {
      return new BigDecimal(v);
    } else if (o instanceof Byte || o instanceof Short || o instanceof Integer || o instanceof Long) {
      return BigDecimal.valueOf((long) o);
    } else if (o instanceof Number || o instanceof String) {
      try {
        return new BigDecimal(o.toString());
      } catch (NumberFormatException e) {
        throw IllegalArgumentExceptions.forWrongPattern(fieldName);
      }
    }
    throw IllegalArgumentExceptions.forWrongClass(fieldName);
  }

  @JsonValue
  public BigDecimal getValue() {
    return this.value;
  }

  public String toPlainString() {
    // toString可能会是科学计数法表示 1、使用科学计数法表示创建对象；2、scale设置为负数；3、执行stripTrailingZeros
    // toPlainString则将会是原生数字表示而不是科学计数法
    return this.value.toPlainString();
  }

  /**
   * Number的方法，不需要实现缓存 <br>
   * 因为BigDecimal本身已实现缓存 <br>
   */
  @Override
  public int intValue() {
    return this.value.intValue();
  }

  @Override
  public long longValue() {
    return this.value.longValue();
  }

  @Override
  public float floatValue() {
    return this.value.floatValue();
  }

  @Override
  public double doubleValue() {
    return this.value.doubleValue();
  }
}
