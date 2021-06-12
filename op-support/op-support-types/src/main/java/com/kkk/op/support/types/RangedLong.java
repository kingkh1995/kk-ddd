package com.kkk.op.support.types;

import com.kkk.op.support.exception.IllegalArgumentExceptions;
import com.kkk.op.support.marker.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
public abstract class RangedLong implements Type {

  @Getter // 设置为protected以便子类使用，使用基本数据类型
  protected long value;

  /**
   * 默认含头不含尾
   *
   * @param l 可以为Null
   * @param fieldName 字段名称
   * @param min 最小值（包含）
   * @param max 最大值（不包含）
   */
  protected RangedLong(Long l, String fieldName, Long min, Long max) {
    if (l == null) {
      throw IllegalArgumentExceptions.forNull(fieldName);
    }
    if (min != null && l < min) {
      throw IllegalArgumentExceptions.forMinValue(fieldName, min, true);
    }
    if (max != null && l >= max) {
      throw IllegalArgumentExceptions.forMaxValue(fieldName, max, false);
    }
    this.value = l;
  }

  protected static long parseLong(String s, String fieldName) {
    if (s == null || s.isBlank()) {
      throw IllegalArgumentExceptions.forNull(fieldName);
    }
    try {
      return Long.parseLong(s);
    } catch (NumberFormatException e) {
      throw IllegalArgumentExceptions.forWrongPattern(fieldName);
    }
  }
}
