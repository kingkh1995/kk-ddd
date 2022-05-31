package com.kk.ddd.user.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.kk.ddd.support.type.LongId;
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
  public static UserId of(long l) {
    return new UserId(l, "UserId");
  }

  public static UserId valueOf(Object o, String fieldName) {
    return new UserId(parseLong(o, fieldName), fieldName);
  }
}
