package com.kk.ddd.support.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.kk.ddd.support.core.Identifier;
import lombok.EqualsAndHashCode;

/**
 * long类型Id
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true) // 重写EqualsAndHashCode
public class LongId extends RangedLong implements Identifier {

  protected LongId(long value, String fieldName) {
    super(value, fieldName, 0L, false, null, null);
  }

  @JsonCreator
  public static LongId of(long l) {
    return new LongId(l, "LongId");
  }

  public static LongId valueOf(Object o, String fieldName) {
    return new LongId(parseLong(o, fieldName), fieldName);
  }

  @Override
  public String identifier() {
    return String.valueOf(getValue());
  }
}
