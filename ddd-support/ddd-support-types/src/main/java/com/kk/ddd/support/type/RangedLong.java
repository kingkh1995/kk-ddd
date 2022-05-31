package com.kk.ddd.support.type;

import com.fasterxml.jackson.annotation.JsonValue;
import com.kk.ddd.support.marker.Type;
import com.kk.ddd.support.util.IllegalArgumentExceptions;
import lombok.EqualsAndHashCode;

/**
 * 有范围长整型DP类基类 <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true)
public abstract class RangedLong extends Number implements Type {

  private final long value;

  /**
   * @param value 数值
   * @param fieldName 字段名称
   * @param min 最小值
   * @param minInclusive 是否包含最小值
   * @param max 最大值
   * @param maxInclusive 是否包含最大值
   */
  protected RangedLong(
      long value,
      String fieldName,
      Long min,
      Boolean minInclusive,
      Long max,
      Boolean maxInclusive) {
    if (min != null) {
      var cmp = Long.compare(value, min);
      if ((minInclusive && cmp < 0) || (!minInclusive && cmp <= 0)) {
        throw IllegalArgumentExceptions.forMinValue(fieldName, min, minInclusive);
      }
    }
    if (max != null) {
      var cmp = Long.compare(value, max);
      if ((maxInclusive && cmp > 0) || (!maxInclusive && cmp >= 0)) {
        throw IllegalArgumentExceptions.forMaxValue(fieldName, max, maxInclusive);
      }
    }
    this.value = value;
  }

  // 自定义Jackson序列化，也可以注释到字段（最好不要，因为无法被子类覆盖）
  // 如果父类存在注解，需要声明 @JsonValue(false) 覆盖父类，不然会报错重复定义
  @JsonValue
  public long getValue() {
    return this.value;
  }

  protected static long parseLong(Object o, String fieldName) {
    if (o == null) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    } else if (o instanceof Long l) {
      return l;
    } else if (o instanceof String s) {
      try {
        return Long.parseLong(s);
      } catch (NumberFormatException e) {
        throw IllegalArgumentExceptions.forWrongPattern(fieldName);
      }
    }
    throw IllegalArgumentExceptions.forWrongClass(fieldName);
  }

  @Override
  public int intValue() {
    return (int) this.value;
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
    return (double) this.value;
  }
}
