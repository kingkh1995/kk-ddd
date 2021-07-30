package com.kkk.op.support.types;

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
public class TenThousandYuan extends SpecificNumber {

  protected TenThousandYuan(BigDecimal value, String fieldName) {
    super(value, fieldName, ZERO, true, null, null, -2);
  }

  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.00");

  protected transient String formattedStringCache; // 字符串缓存

  /** 格式化表示 */
  public String toFormattedString() {
    if (this.formattedStringCache == null) {
      this.formattedStringCache = DECIMAL_FORMAT.format(this.value.movePointLeft(4)) + "（万元）";
    }
    return this.formattedStringCache;
  }

  private static TenThousandYuan of(@NotNull BigDecimal value, String fieldName) {
    return new TenThousandYuan(value, fieldName);
  }

  private static TenThousandYuan ofTenThousand(@NotNull BigDecimal value, String fieldName) {
    return new TenThousandYuan(value.movePointRight(4), fieldName);
  }

  // 针对可靠输入的 from 方法
  public static TenThousandYuan from(@NotNull BigDecimal value) {
    return of(value, null);
  }

  public static TenThousandYuan fromTenThousand(@NotNull BigDecimal value) {
    return ofTenThousand(value, null);
  }

  // 针对不可靠输入的 valueOf 方法

  public static TenThousandYuan valueOf(String s, String fieldName) {
    return of(parse(s, fieldName), fieldName);
  }

  public static TenThousandYuan valueOfTenThousand(String s, String fieldName) {
    return ofTenThousand(parse(s, fieldName), fieldName);
  }
}
