package com.kk.ddd.support.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.kk.ddd.support.constant.Constants;
import lombok.EqualsAndHashCode;

/**
 * 版本号 <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true)
public final class Version extends RangedInt implements Comparable<Version> {

  // 初始版本号：0
  public static final Version PRIMARY = of(0, "");

  /** 缓存内部类：单检查懒加载（可以忍受重复创建） */
  private static class Cache {

    // spi拓展，支持修改缓存上限。
    static final Version[] cache =
        new Version[Math.max(100, 1 + Constants.TYPE.versionCacheHigh())];

    static Version get(int i) {
      var v = Cache.cache[i];
      if (v == null) {
        v = cache[i] = new Version(i, "");
      }
      return v;
    }
  }

  private Version(int value, String fieldName) {
    super(value, fieldName, 0, true, null, null);
  }

  private static Version of(int value, String fieldName) {
    if (value >= 0 && value < Cache.cache.length) {
      return Cache.get(value);
    }
    return new Version(value, fieldName);
  }

  @JsonCreator
  public static Version of(int i) {
    return of(i, "Version");
  }

  public static Version valueOf(Object o, String fieldName) {
    return of(parseInt(o, fieldName), fieldName);
  }

  @Override
  public int compareTo(Version o) {
    return Integer.compare(this.getValue(), o.getValue());
  }

  public Version next() {
    return of(this.getValue() + 1);
  }
}