package com.kkk.op.support.types;

import com.kkk.op.support.marker.Identifier;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * long类型Id
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true) // 重写EqualsAndHashCode
public class LongId extends SpecificNumber implements Identifier {

  protected LongId(@NotNull BigDecimal value, String fieldName) {
    super(value, fieldName, ZERO, false, null, null, 0);
  }

  /**
   * 不对外提供构造函数，只提供 valueOf（不可靠输入） 和 of（可靠输入） 静态方法 <br>
   * <br>
   */
  protected static LongId of(@NotNull BigDecimal value, String fieldName) {
    return new LongId(value, fieldName);
  }

  // 针对可靠输入的 of 方法
  public static LongId of(long id) {
    return of(new BigDecimal(id), "");
  }

  // 针对不可靠输入的 valueOf 方法
  public static LongId valueOf(Long l, String fieldName) {
    return of(parse(l, fieldName), fieldName);
  }

  public static LongId valueOf(String s, String fieldName) {
    return of(parse(s, fieldName), fieldName);
  }
}
