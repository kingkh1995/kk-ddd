package com.kk.ddd.user.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kk.ddd.support.core.Identifier;
import com.kk.ddd.support.util.ParseUtils;
import com.kk.ddd.support.util.ValidateUtils;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserId implements Identifier, Comparable<UserId> {

  @Getter @JsonValue private final long value;

  private static UserId of(final long value, final String fieldName) {
    ValidateUtils.minValue(value, 0, false, fieldName);
    return new UserId(value);
  }

  @JsonCreator
  public static UserId of(final long l) {
    return of(l, "UserId");
  }

  public static UserId valueOf(final Object o, final String fieldName) {
    return of(ParseUtils.parseLong(o, fieldName), fieldName);
  }

  @Override
  public String identifier() {
    return String.valueOf(this.getValue());
  }

  @Override
  public int compareTo(UserId o) {
    return Long.compare(this.getValue(), o.getValue());
  }
}
