package com.kkk.op.user.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.kkk.op.support.type.LongId;
import lombok.EqualsAndHashCode;

/**
 * 账户ID <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true)
public final class AccountId extends LongId {

  private AccountId(long value, String fieldName) {
    super(value, fieldName);
  }

  @JsonCreator
  public static AccountId from(long l) {
    return new AccountId(l, "AccountId");
  }

  public static AccountId valueOf(Long l, String fieldName) {
    return new AccountId(parseLong(l, fieldName), fieldName);
  }

  public static AccountId valueOf(String s, String fieldName) {
    return new AccountId(parseLong(s, fieldName), fieldName);
  }
}
