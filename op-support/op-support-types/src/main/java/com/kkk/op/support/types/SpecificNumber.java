package com.kkk.op.support.types;

import com.kkk.op.support.exception.IllegalArgumentExceptions;
import com.kkk.op.support.marker.Type;
import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * 特定数字DP类基类 <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
public abstract class SpecificNumber implements Type {

  protected static final BigDecimal ZERO = BigDecimal.ZERO;
  protected static final BigDecimal TEN = BigDecimal.TEN;
  protected static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

  protected final BigDecimal value;

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
      @NotNull BigDecimal value,
      String fieldName,
      BigDecimal min,
      Boolean includeMin,
      BigDecimal max,
      Boolean includeMax,
      Integer scale) {
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
    if (scale != null) {
      // 先去掉尾部的0 并可能会被转为科学计数法表示
      value = value.stripTrailingZeros();
      if (value.scale() > scale) {
        throw IllegalArgumentExceptions.forScaleAbove(fieldName, scale);
      }
      value = value.setScale(scale); // 最后设置sacle补全后面的0 同时保证scale非负时为原生数字表示 负数则为科学计数法表示
    }
    this.value = Objects.requireNonNull(value);
  }

  protected static BigDecimal parse(BigDecimal decimal, String fieldName) {
    if (decimal == null) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    return decimal;
  }

  protected static BigDecimal parse(Integer i, String fieldName) {
    if (i == null) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    return new BigDecimal(i);
  }

  protected static BigDecimal parse(Long l, String fieldName) {
    if (l == null) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    return new BigDecimal(l);
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

  public BigDecimal value() {
    return this.value;
  }

  /**
   * 以下方法不需要实现缓存 <br>
   * 因为BigDecimal本身已实现缓存 <br>
   */
  public int intValue() {
    return this.value.intValue();
  }

  public long longValue() {
    return this.value.longValue();
  }

  private transient String plainStringCache; // plainString缓存 BigDecimal有toString缓存

  public String stringValue() {
    // toString可能会是科学计数法表示 1、使用科学计数法表示创建对象；2、scale设置为负数；3、执行stripTrailingZeros
    // toPlainString则将会是原生数字表示而不是科学计数法
    return this.value.toString();
  }
}