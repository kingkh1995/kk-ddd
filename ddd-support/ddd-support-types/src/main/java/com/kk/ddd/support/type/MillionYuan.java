package com.kk.ddd.support.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kk.ddd.support.core.Type;
import com.kk.ddd.support.util.ParseUtils;
import com.kk.ddd.support.util.ValidateUtils;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 人民币万元（只允许带两位小数即精确到百元位） <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MillionYuan implements Type, Comparable<MillionYuan> {

  @Getter
  @JsonValue private final BigDecimal value;

  private static MillionYuan of(@NotNull BigDecimal value, String fieldName) {
    ValidateUtils.scaleAbove(value, 2, fieldName);
    return new MillionYuan(value);
  }

  // 针对可靠输入的 from 方法
  @JsonCreator
  public static MillionYuan of(@NotNull BigDecimal value) {
    return of(value, "TenThousandYuan");
  }

  // 针对不可靠输入的 valueOf 方法
  public static MillionYuan valueOf(Object o, String fieldName) {
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

  private static final DecimalFormat FORMAT = new DecimalFormat("#,###.00");

  protected transient volatile String formattedStringCache;

  public String toFormattedString() {
    if (this.formattedStringCache == null) {
      this.formattedStringCache = FORMAT.format(this.getValue()) + "（万元）";
    }
    return this.formattedStringCache;
  }
}
