package com.kkk.op.support.type;

import com.fasterxml.jackson.annotation.JsonValue;
import com.kkk.op.support.exception.IllegalArgumentExceptions;
import com.kkk.op.support.marker.Type;
import lombok.EqualsAndHashCode;

/**
 * 有范围长整型DP类基类 <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
public abstract class RangedLong implements Type {

  private final long value;

  /**
   * @param value 数值
   * @param fieldName 字段名称
   * @param min 最小值
   * @param includeMin 是否包含最小值
   * @param max 最大值
   * @param includeMax 是否包含最大值
   */
  protected RangedLong(
      long value, String fieldName, Long min, Boolean includeMin, Long max, Boolean includeMax) {
    if (min != null) {
      var cmp = Long.compare(value, min);
      if ((includeMin && cmp < 0) || (!includeMin && cmp <= 0)) {
        throw IllegalArgumentExceptions.forMinValue(fieldName, min, includeMin);
      }
    }
    if (max != null) {
      var cmp = Long.compare(value, max);
      if ((includeMax && cmp > 0) || (!includeMax && cmp >= 0)) {
        throw IllegalArgumentExceptions.forMaxValue(fieldName, max, includeMax);
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

  protected static long parseLong(Long l, String fieldName) {
    if (l == null) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    return l;
  }

  protected static long parseLong(String s, String fieldName) {
    if (s == null || s.isEmpty()) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    try {
      return Long.parseLong(s);
    } catch (NumberFormatException e) {
      throw IllegalArgumentExceptions.forMustNumber(fieldName);
    }
  }
}
