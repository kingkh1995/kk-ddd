package com.kkk.op.user.domain.types;

import com.kkk.op.support.enums.AccountStatusEnum;
import com.kkk.op.support.exception.IllegalArgumentExceptions;
import com.kkk.op.support.marker.Type;
import java.util.Arrays;
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
public class AccountStatus implements Type {

  @Getter private final AccountStatusEnum value;

  private AccountStatus(@NotNull AccountStatusEnum value) {
    this.value = value;
  }

  /** 缓存内部类 */
  private static class Cache {

    static final AccountStatus[] cache;

    static {
      cache =
          Arrays.stream(AccountStatusEnum.values())
              .map(AccountStatus::new)
              .toArray(AccountStatus[]::new); // 传入IntFunction 参数为数组大小
    }
  }

  /** of方法和valueOf方法 */
  public static AccountStatus of(@NotNull AccountStatusEnum accountStatusEnum) {
    return Cache.cache[accountStatusEnum.ordinal()];
  }

  public static AccountStatus valueOf(String s, String fieldName) {
    if (s == null || s.isBlank()) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    try {
      // 如果不存在对应枚举，valueOf方法不会返回 null，而是抛出异常
      return of(AccountStatusEnum.valueOf(s));
    } catch (IllegalArgumentException e) {
      throw IllegalArgumentExceptions.forInvalidEnum(fieldName);
    }
  }

  public static AccountStatus valueOf(AccountStatusEnum accountStatusEnum, String fieldName) {
    if (accountStatusEnum == null) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    return of(accountStatusEnum);
  }
}
