package com.kk.ddd.user.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kk.ddd.support.core.Identifier;
import com.kk.ddd.support.util.ParseUtils;
import com.kk.ddd.support.util.ValidateUtils;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 账户ID <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccountId implements Identifier, Comparable<AccountId> {

  @Getter @JsonValue private final long value;

  private static AccountId of(long value, String fieldName) {
    ValidateUtils.minValue(value, 0, false, fieldName);
    return new AccountId(value);
  }

  @JsonCreator
  public static AccountId of(long l) {
    return of(l, "AccountId");
  }

  public static AccountId valueOf(Object o, String fieldName) {
    return of(ParseUtils.parseLong(o, fieldName), fieldName);
  }

  @Override
  public String identifier() {
    return String.valueOf(this.getValue());
  }

  @Override
  public int compareTo(AccountId o) {
    return Long.compare(this.getValue(), o.getValue());
  }
}
