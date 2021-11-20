package com.kkk.op.support.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.kkk.op.support.marker.Identifier;
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
  public static LongId from(long l) {
    return new LongId(l, "LongId");
  }

  public static LongId valueOf(Long l, String fieldName) {
    return new LongId(parseLong(l, fieldName), fieldName);
  }

  public static LongId valueOf(String s, String fieldName) {
    return new LongId(parseLong(s, fieldName), fieldName);
  }

  @Override
  public String identifier() {
    return String.valueOf(getValue());
  }
}
