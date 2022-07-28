package com.kk.ddd.support.type;

import com.fasterxml.jackson.annotation.JsonValue;
import com.kk.ddd.support.core.Type;
import com.kk.ddd.support.util.IllegalArgumentExceptions;

/**
 * 有范围整型DP类基类 <br>
 *
 * @author KaiKoo
 */
public class RangedInt extends Number implements Type {

  private final int value;

  /**
   * @param value 数值
   * @param fieldName 字段名称
   * @param min 最小值
   * @param minInclusive 是否包含最小值
   * @param max 最大值
   * @param maxInclusive 是否包含最大值
   */
  protected RangedInt(
      int value,
      String fieldName,
      Integer min,
      Boolean minInclusive,
      Integer max,
      Boolean maxInclusive) {
    if (min != null) {
      var cmp = Integer.compare(value, min);
      if ((minInclusive && cmp < 0) || (!minInclusive && cmp <= 0)) {
        throw IllegalArgumentExceptions.forMinValue(fieldName, min, minInclusive);
      }
    }
    if (max != null) {
      var cmp = Integer.compare(value, max);
      if ((maxInclusive && cmp > 0) || (!maxInclusive && cmp >= 0)) {
        throw IllegalArgumentExceptions.forMaxValue(fieldName, max, maxInclusive);
      }
    }
    this.value = value;
  }

  @JsonValue
  public int getValue() {
    return this.value;
  }

  protected static int parseInt(Object o, String fieldName) {
    if (o == null) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    } else if (o instanceof Integer i) {
      return i;
    } else if (o instanceof String s) {
      try {
        return Integer.parseInt(s);
      } catch (NumberFormatException e) {
        throw IllegalArgumentExceptions.forWrongPattern(fieldName);
      }
    }
    throw IllegalArgumentExceptions.forWrongClass(fieldName);
  }

  @Override
  public int intValue() {
    return this.value;
  }

  @Override
  public long longValue() {
    return this.value;
  }

  @Override
  public float floatValue() {
    return (float) this.value;
  }

  @Override
  public double doubleValue() {
    return this.value;
  }
}
