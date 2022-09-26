package com.kk.ddd.support.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kk.ddd.support.core.Type;
import com.kk.ddd.support.util.ParseUtils;
import com.kk.ddd.support.util.ValidateUtils;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 人民币万元（精确到百元位即只允许带两位小数） <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MillionYuan implements Type, Comparable<MillionYuan> {

  @Getter @JsonValue private final BigDecimal value;

  private static MillionYuan of(final BigDecimal value, final String fieldName) {
    ValidateUtils.scaleAbove(value, 2, fieldName);
    return new MillionYuan(value);
  }

  @JsonCreator
  public static MillionYuan of(final BigDecimal value) {
    return of(value, "MillionYuan");
  }

  public static MillionYuan valueOf(final Object o, final String fieldName) {
    return of(ParseUtils.parseBigDecimal(o, fieldName), fieldName);
  }

  @JsonValue
  public BigDecimal toPrimaryValue() {
    return this.getValue().movePointLeft(4);
  }

  @Override
  public int compareTo(MillionYuan o) {
    return this.getValue().compareTo(o.getValue());
  }

  /** 格式化表示 */
  private static final DecimalFormat FORMAT = new DecimalFormat("#,###.00（万元）");

  protected transient volatile String formattedStringCache;

  public String toFormattedString() {
    if (this.formattedStringCache == null) {
      this.formattedStringCache = FORMAT.format(this.getValue());
    }
    return this.formattedStringCache;
  }
}
