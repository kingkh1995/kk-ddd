package com.kk.ddd.user.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.kk.ddd.support.type.LongId;
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
  public static AccountId of(long l) {
    return new AccountId(l, "AccountId");
  }

  public static AccountId valueOf(Object o, String fieldName) {
    return new AccountId(parseLong(o, fieldName), fieldName);
  }
}
