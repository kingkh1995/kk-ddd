package com.kkk.op.user.domain.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.kkk.op.support.type.LongId;
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

  private AccountId(@NotNull BigDecimal value, String fieldName) {
    super(value, fieldName);
  }

  @JsonCreator
  public static AccountId from(long l) {
    return new AccountId(new BigDecimal(l), "AccountId");
  }

  public static AccountId valueOf(Long l, String fieldName) {
    return new AccountId(parseBigDecimal(l, fieldName), fieldName);
  }

  public static AccountId valueOf(String s, String fieldName) {
    return new AccountId(parseBigDecimal(s, fieldName), fieldName);
  }
}
