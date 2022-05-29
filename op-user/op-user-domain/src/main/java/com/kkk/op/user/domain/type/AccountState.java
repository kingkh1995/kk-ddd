package com.kkk.op.user.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kkk.op.support.enums.AccountStateEnum;
import com.kkk.op.support.marker.Type;
import com.kkk.op.support.util.IllegalArgumentExceptions;
import java.util.Arrays;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 枚举值也封装为DP <br>
 * 为枚举值类DP添加内部缓存，参考包装类的缓存设计
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
public final class AccountState implements Type {

  @Getter @JsonValue private final AccountStateEnum value;

  private AccountState(@NotNull AccountStateEnum value) {
    this.value = value;
  }

  /** 缓存内部类 */
  private static class Cache {

    static final AccountState[] cache;

    static {
      cache =
          Arrays.stream(AccountStateEnum.values())
              .map(AccountState::new)
              .toArray(AccountState[]::new); // 传入IntFunction 参数为数组大小
    }
  }

  @JsonCreator
  public static AccountState of(@NotNull AccountStateEnum accountStateEnum) {
    return Cache.cache[accountStateEnum.ordinal()];
  }

  public static AccountState valueOf(Object o, String fieldName) {
    if (o == null) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }else if (o instanceof AccountStateEnum accountStateEnum) {
      return of(accountStateEnum);
    } else if (o instanceof String s) {
      try {
        // 如果不存在对应枚举，valueOf方法不会返回 null，而是抛出异常
        return of(AccountStateEnum.valueOf(s));
      } catch (IllegalArgumentException e) {
        throw IllegalArgumentExceptions.forInvalidEnum(fieldName);
      }
    }
    throw IllegalArgumentExceptions.forWrongClass(fieldName);
  }
}
