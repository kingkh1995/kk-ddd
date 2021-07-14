package com.kkk.op.user.domain.types;

import com.kkk.op.support.enums.AccountStatusEnum;
import com.kkk.op.support.exception.IllegalArgumentExceptions;
import com.kkk.op.support.marker.Type;
import java.util.Arrays;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 枚举值也封装为DP <br>
 * 枚举值类DP添加内部缓存 参考包装类的缓存设计
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
public class AccountStatus implements Type {

  @Getter protected final AccountStatusEnum value;

  private AccountStatus(AccountStatusEnum value) {
    this.value = value;
  }

  public static AccountStatus valueOf(String s, String fieldName) {
    if (s == null || s.isBlank()) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    // valueOf 方法不会返回 null，会抛出异常
    try {
      return Cache.get(AccountStatusEnum.valueOf(s));
    } catch (IllegalArgumentException e) {
      throw IllegalArgumentExceptions.forInvalidEnum(fieldName);
    }
  }

  public static AccountStatus valueOf(AccountStatusEnum accountStatusEnum, String fieldName) {
    if (accountStatusEnum == null) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    return Cache.get(accountStatusEnum);
  }

  public static AccountStatus of(AccountStatusEnum accountStatusEnum) {
    return Cache.get(accountStatusEnum);
  }

  /** 参考Integer等添加一个缓存内部类 */
  private static class Cache {

    static final AccountStatus[] cache;

    static {
      // todo... 使用 Stream Collector
      cache = new AccountStatus[AccountStatusEnum.values().length];
      Arrays.stream(AccountStatusEnum.values())
          .forEach(e -> cache[e.ordinal()] = new AccountStatus(e));
    }

    public static AccountStatus get(AccountStatusEnum accountStatusEnum) {
      return cache[accountStatusEnum.ordinal()];
    }
  }
}
