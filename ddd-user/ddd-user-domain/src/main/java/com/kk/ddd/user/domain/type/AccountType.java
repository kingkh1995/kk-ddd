package com.kk.ddd.user.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kk.ddd.support.constant.AccountTypeEnum;
import com.kk.ddd.support.core.Type;
import com.kk.ddd.support.util.ParseUtils;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccountType implements Type, Comparable<AccountType> {

  @JsonValue private final AccountTypeEnum value;

  /** 缓存内部类 */
  private static class Cache {

    static final AccountType[] cache =
        Arrays.stream(AccountTypeEnum.values()).map(AccountType::new).toArray(AccountType[]::new);
  }

  @JsonCreator
  public static AccountType of(final AccountTypeEnum accountTypeEnum) {
    return Cache.cache[accountTypeEnum.ordinal()];
  }

  public static AccountType valueOf(final Object o, final String fieldName) {
    return of(ParseUtils.parseEnum(AccountTypeEnum.class, o, fieldName));
  }

  @Override
  public int compareTo(AccountType o) {
    return this.value.compareTo(o.value);
  }

  public AccountTypeEnum toEnum() {
    return this.value;
  }
}
