package com.kkk.op.user.domain.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.kkk.op.support.types.LongId;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true)
public final class UserId extends LongId {

  private UserId(@NotNull BigDecimal value, String fieldName) {
    super(value, fieldName);
  }

  @JsonCreator
  public static UserId from(long l) {
    return new UserId(new BigDecimal(l), "UserId");
  }

  public static UserId valueOf(Long l, String fieldName) {
    return new UserId(parseBigDecimal(l, fieldName), fieldName);
  }

  public static UserId valueOf(String s, String fieldName) {
    return new UserId(parseBigDecimal(s, fieldName), fieldName);
  }
}
