package com.kkk.op.user.domain.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.kkk.op.support.types.LongId;
import lombok.EqualsAndHashCode;

/**
 * <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true)
public final class UserId extends LongId {

  private UserId(long value, String fieldName) {
    super(value, fieldName);
  }

  @JsonCreator
  public static UserId from(long l) {
    return new UserId(l, "UserId");
  }

  public static UserId valueOf(Long l, String fieldName) {
    return new UserId(parseLong(l, fieldName), fieldName);
  }

  public static UserId valueOf(String s, String fieldName) {
    return new UserId(parseLong(s, fieldName), fieldName);
  }
}
