package com.kk.ddd.user.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kk.ddd.support.core.Type;
import com.kk.ddd.support.util.ParseUtils;
import com.kk.ddd.support.util.ValidateUtils;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 验证强度 <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthStrength implements Type, Comparable<AuthStrength> {

  @Getter @JsonValue private final int value;

  private static final int BOUND = 10;

  private static class Cache {
    static final AuthStrength[] cache =
        IntStream.range(0, BOUND).mapToObj(AuthStrength::new).toArray(AuthStrength[]::new);
  }

  public static final AuthStrength MIN = Cache.cache[0];

  public static final AuthStrength MAX = Cache.cache[BOUND - 1];

  private static AuthStrength of(final int value, final String fieldName) {
    ValidateUtils.minValue(value, 0, true, fieldName);
    ValidateUtils.maxValue(value, BOUND, false, fieldName);
    return Cache.cache[value];
  }

  @JsonCreator
  public static AuthStrength of(final int i) {
    return of(i, "AuthStrength");
  }

  public static AuthStrength valueOf(final Object o, final String fieldName) {
    return of(ParseUtils.parseInt(o, fieldName), fieldName);
  }

  @Override
  public int compareTo(AuthStrength o) {
    return Integer.compare(this.getValue(), o.getValue());
  }

  public AuthStrength growUp() {
    if (this.getValue() == MAX.getValue()) {
      throw new IllegalStateException("already max!");
    }
    return of(this.getValue() + 1);
  }

  public AuthStrength growDown() {
    if (this.getValue() == MIN.getValue()) {
      throw new IllegalStateException("already min!");
    }
    return of(this.getValue() - 1);
  }
}
