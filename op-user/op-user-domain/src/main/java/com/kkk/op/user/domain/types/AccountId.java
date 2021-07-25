package com.kkk.op.user.domain.types;

import com.kkk.op.support.types.LongId;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * 账户ID <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true)
public final class AccountId extends LongId {

  protected AccountId(BigDecimal value) {
    super(value, "accountId");
  }

  private static AccountId of(@NotNull BigDecimal value) {
    return new AccountId(value);
  }

  // 针对可靠输入的 of 方法
  public static AccountId of(long id) {
    return new AccountId(new BigDecimal(id));
  }

  // 针对不可靠输入的 valueOf 方法
  public static AccountId from(Long l) {
    return of(parse(l, "accountId"));
  }

  public static AccountId from(String s) {
    return of(parse(s, "accountId"));
  }
}
