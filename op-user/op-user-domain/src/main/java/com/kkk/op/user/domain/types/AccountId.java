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

  private AccountId(BigDecimal value) {
    super(value, "accountId");
  }

  private static AccountId of(@NotNull BigDecimal value) {
    return new AccountId(value);
  }

  public static AccountId from(long id) {
    return new AccountId(new BigDecimal(id));
  }

  public static AccountId valueOf(Long l) {
    return of(parse(l, "accountId"));
  }

  public static AccountId valueOf(String s) {
    return of(parse(s, "accountId"));
  }
}
