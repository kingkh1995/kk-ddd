package com.kk.ddd.user.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kk.ddd.support.core.Type;
import com.kk.ddd.support.enums.AccountStateEnum;
import com.kk.ddd.support.util.ParseUtils;
import java.util.Arrays;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * 枚举值也封装为DP <br>
 * 为枚举值类DP添加内部缓存，参考包装类的缓存设计
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccountState implements Type {

  @JsonValue private final AccountStateEnum value;

  /** 缓存内部类 */
  private static class Cache {

    static final AccountState[] cache = Arrays.stream(AccountStateEnum.values())
            .map(AccountState::new)
              .toArray(AccountState[]::new);

  }

  @JsonCreator
  public static AccountState of(@NotNull AccountStateEnum accountStateEnum) {
    return Cache.cache[accountStateEnum.ordinal()];
  }

  public static AccountState valueOf(Object o, String fieldName) {
    return of(ParseUtils.parseEnum(AccountStateEnum.class, o, fieldName));
  }

  public AccountStateEnum toEnum() {
    return this.value;
  }
}
