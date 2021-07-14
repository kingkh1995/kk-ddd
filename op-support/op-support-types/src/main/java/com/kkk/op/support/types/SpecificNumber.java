package com.kkk.op.support.types;

import com.kkk.op.support.exception.IllegalArgumentExceptions;
import com.kkk.op.support.marker.Type;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;

/**
 * 特定数字DP类基类 <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
public abstract class SpecificNumber implements Type {

  protected static final transient BigDecimal ZERO = BigDecimal.ZERO;
  protected static final transient BigDecimal TEN = new BigDecimal("10");
  protected static final transient BigDecimal ONE_HUNDRED = new BigDecimal("100");

  protected BigDecimal value;

  /**
   * @param value 数值
   * @param fieldName 字段名称
   * @param min 最小值 Nullable
   * @param includeMin 是否包含最小值
   * @param max 最大值 Nullable
   * @param includeMax 是否包含最大值
   * @param scale 小数位
   */
  protected SpecificNumber(
      BigDecimal value,
      String fieldName,
      BigDecimal min,
      Boolean includeMin,
      BigDecimal max,
      Boolean includeMax,
      int scale) {
    if (value == null) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    if (min != null) {
      var cmp = value.compareTo(min);
      if ((includeMin && cmp < 0) || (!includeMin && cmp <= 0)) {
        throw IllegalArgumentExceptions.forMinValue(fieldName, min, includeMin);
      }
    }
    if (max != null) {
      var cmp = value.compareTo(max);
      if ((includeMax && cmp > 0) || (!includeMax && cmp >= 0)) {
        throw IllegalArgumentExceptions.forMaxValue(fieldName, max, includeMax);
      }
    }
    if (value.scale() > scale) {
      throw IllegalArgumentExceptions.forAtMostScale(fieldName, scale);
    }
    this.value = value;
  }

  protected static BigDecimal parse(String s, String fieldName) {
    if (s == null || s.isEmpty()) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    try {
      return new BigDecimal(s);
    } catch (NumberFormatException e) {
      throw IllegalArgumentExceptions.forMustNumber(fieldName);
    }
  }

  protected static BigDecimal parse(Long l, String fieldName) {
    if (l == null) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    return new BigDecimal(l);
  }

  protected static BigDecimal parse(Integer i, String fieldName) {
    if (i == null) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    return new BigDecimal(i);
  }

  public BigDecimal value() {
    return this.value;
  }

  public int intValue() {
    return this.value.intValue();
  }

  public long longValue() {
    return this.value.longValue();
  }

  public String stringValue() {
    return this.value.toString();
  }
}
