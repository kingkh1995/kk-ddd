package com.kk.ddd.user.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kk.ddd.support.core.Type;
import com.kk.ddd.support.enums.UserStateEnum;
import com.kk.ddd.support.util.ParseUtils;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * 用户状态 <br>
 * 枚举值也封装为DP，并为枚举值类DP添加内部缓存，参考包装类的缓存设计。
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserState implements Type, Comparable<UserState> {

  @JsonValue private final UserStateEnum value;

  /** 缓存内部类 */
  private static class Cache {

    static final UserState[] cache =
        Arrays.stream(UserStateEnum.values()).map(UserState::new).toArray(UserState[]::new);
  }

  @JsonCreator
  public static UserState of(final UserStateEnum userStateEnum) {
    return Cache.cache[userStateEnum.ordinal()];
  }

  public static UserState valueOf(final Object o, final String fieldName) {
    return of(ParseUtils.parseEnum(UserStateEnum.class, o, fieldName));
  }

  @Override
  public int compareTo(UserState o) {
    return this.value.compareTo(o.value);
  }

  public UserStateEnum toEnum() {
    return this.value;
  }
}
