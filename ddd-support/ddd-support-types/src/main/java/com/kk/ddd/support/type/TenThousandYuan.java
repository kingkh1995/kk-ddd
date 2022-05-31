package com.kk.ddd.support.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * 人民币万元（只能带两位小数即精确到百位） <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true)
public class TenThousandYuan extends SpecificDecimal implements Comparable<TenThousandYuan> {

  private final BigDecimal valueBy10k; // 万元值缓存

  protected TenThousandYuan(@NotNull BigDecimal valueBy10k, String fieldName) {
    super(valueBy10k.movePointRight(4), fieldName, BigDecimal.ZERO, true, null, null, -2);
    this.valueBy10k = valueBy10k;
  }

  // 针对可靠输入的 from 方法
  @JsonCreator
  public static TenThousandYuan of(@NotNull BigDecimal valueBy10k) {
    return new TenThousandYuan(valueBy10k, "TenThousandYuan");
  }

  // 针对不可靠输入的 valueOf 方法
  public static TenThousandYuan valueOf(Object o, String fieldName) {
    return new TenThousandYuan(parseBigDecimal(o, fieldName), fieldName);
  }

  @JsonValue(false) // 声明覆盖父类注解，不然会报错重复定义
  @Override
  public BigDecimal getValue() {
    return super.getValue();
  }

  @JsonValue
  public BigDecimal getValueBy10k() {
    return this.valueBy10k;
  }

  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.00");

  protected transient String formattedStringCache; // 字符串缓存

  /** 格式化表示 */
  public String toFormattedString() {
    if (this.formattedStringCache == null) {
      this.formattedStringCache = DECIMAL_FORMAT.format(getValueBy10k()) + "（万元）";
    }
    return this.formattedStringCache;
  }

  @Override
  public int compareTo(TenThousandYuan o) {
    return this.getValueBy10k().compareTo(o.getValueBy10k());
  }
}
