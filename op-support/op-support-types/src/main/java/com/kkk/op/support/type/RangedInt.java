package com.kkk.op.support.type;

import com.fasterxml.jackson.annotation.JsonValue;
import com.kkk.op.support.exception.IllegalArgumentExceptions;
import com.kkk.op.support.marker.Type;

/**
 * 有范围整型DP类基类 <br>
 *
 * @author KaiKoo
 */
public class RangedInt implements Type {

  private final int value;

  /**
   * @param value 数值
   * @param fieldName 字段名称
   * @param min 最小值
   * @param includeMin 是否包含最小值
   * @param max 最大值
   * @param includeMax 是否包含最大值
   */
  protected RangedInt(
      int value,
      String fieldName,
      Integer min,
      Boolean includeMin,
      Integer max,
      Boolean includeMax) {
    if (min != null) {
      var cmp = Integer.compare(value, min);
      if ((includeMin && cmp < 0) || (!includeMin && cmp <= 0)) {
        throw IllegalArgumentExceptions.forMinValue(fieldName, min, includeMin);
      }
    }
    if (max != null) {
      var cmp = Integer.compare(value, max);
      if ((includeMax && cmp > 0) || (!includeMax && cmp >= 0)) {
        throw IllegalArgumentExceptions.forMaxValue(fieldName, max, includeMax);
      }
    }
    this.value = value;
  }

  @JsonValue
  public int getValue() {
    return this.value;
  }

  protected static int parseInteger(Integer i, String fieldName) {
    if (i == null) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    return i;
  }

  protected static int parseInteger(String s, String fieldName) {
    if (s == null || s.isEmpty()) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      throw IllegalArgumentExceptions.forMustNumber(fieldName);
    }
  }
}
