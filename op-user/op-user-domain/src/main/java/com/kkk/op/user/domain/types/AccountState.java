package com.kkk.op.user.domain.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kkk.op.support.enums.AccountStateEnum;
import com.kkk.op.support.exception.IllegalArgumentExceptions;
import com.kkk.op.support.marker.Type;
import java.util.Arrays;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 枚举值也封装为DP <br>
 * 枚举值类DP添加内部缓存 参考包装类的缓存设计
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
public final class AccountState implements Type {

  @Getter @JsonValue private final AccountStateEnum value;

  private AccountState(@NotNull AccountStateEnum value) {
    this.value = value;
  }

  /** 缓存内部类：懒加载 */
  @Deprecated
  private static class Cache0 {

    static final AccountState[] cache = new AccountState[AccountStateEnum.values().length];

    static AccountState get0(AccountStateEnum accountStateEnum) {
      var i = Objects.requireNonNull(accountStateEnum).ordinal();
      var v = Cache.cache[i];
      if (v == null) {
        synchronized (Cache0.class) {
          if (cache[i] == null) {
            cache[i] = new AccountState(accountStateEnum);
          }
          v = cache[i];
        }
      }
      return v;
    }
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

  /** of方法和valueOf方法 */
  @JsonCreator
  public static AccountState of(@NotNull AccountStateEnum accountStateEnum) {
    return Cache.cache[Objects.requireNonNull(accountStateEnum).ordinal()];
  }

  public static AccountState valueOf(String s) {
    var fieldName = "accountState";
    if (s == null || s.isBlank()) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    try {
      // 如果不存在对应枚举，valueOf方法不会返回 null，而是抛出异常
      return of(AccountStateEnum.valueOf(s));
    } catch (IllegalArgumentException e) {
      throw IllegalArgumentExceptions.forInvalidEnum(fieldName);
    }
  }

  public static AccountState valueOf(AccountStateEnum accountStateEnum) {
    var fieldName = "accountStateEnum";
    if (accountStateEnum == null) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    return of(accountStateEnum);
  }
}
