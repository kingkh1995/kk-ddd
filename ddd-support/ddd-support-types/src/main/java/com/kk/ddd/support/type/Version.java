package com.kk.ddd.support.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kk.ddd.support.constant.Constants;
import com.kk.ddd.support.core.Type;
import com.kk.ddd.support.util.ParseUtils;
import com.kk.ddd.support.util.ValidateUtils;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 版本号 <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Version implements Type, Comparable<Version> {

  @Getter @JsonValue private final int value;

  // 初始版本号：0
  public static final Version PRIMARY = Cache.cache[0];

  /** 缓存内部类 */
  private static class Cache {

    // spi拓展，支持修改缓存上限。
    private static final Version[] cache =
        IntStream.rangeClosed(0, Math.max(99, Constants.TYPE.versionCacheHigh()))
            .mapToObj(Version::new)
            .toArray(Version[]::new);
  }

  private static Version of(final int value, final String fieldName) {
    ValidateUtils.minValue(value, 0, true, fieldName);
    if (value < Cache.cache.length) {
      return Cache.cache[value];
    }
    return new Version(value);
  }

  @JsonCreator
  public static Version of(final int i) {
    return of(i, "Version");
  }

  public static Version valueOf(final Object o, final String fieldName) {
    return of(ParseUtils.parseInt(o, fieldName), fieldName);
  }

  @Override
  public int compareTo(Version o) {
    return Integer.compare(this.getValue(), o.getValue());
  }

  public Version next() {
    return of(this.getValue() + 1);
  }
}
