package com.kkk.op.support.types;

import com.kkk.op.support.marker.Identifier;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;

/**
 * long类型Id
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true) // 重写EqualsAndHashCode
public class LongId extends SpecificNumber implements Identifier {

  private LongId(BigDecimal value, String fieldName) {
    super(value, fieldName, ZERO, false, null, null, 0);
  }

  /**
   * 不对外提供构造函数，只提供 valueOf（不可靠输入） 和 of（可靠输入） 静态方法 <br>
   * <br>
   */
  public static LongId valueOf(Long l, String fieldName) {
    return new LongId(parse(l, fieldName), fieldName);
  }

  public static LongId valueOf(String s, String fieldName) {
    return new LongId(parse(s, fieldName), fieldName);
  }

  public static LongId of(long id) {
    return new LongId(new BigDecimal(id), "");
  }

  public static LongId of(BigDecimal id) {
    return new LongId(id, "");
  }
}
