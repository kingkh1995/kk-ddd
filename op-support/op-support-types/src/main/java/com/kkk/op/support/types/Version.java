package com.kkk.op.support.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Optional;
import lombok.EqualsAndHashCode;

/**
 * 版本号 <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true)
public final class Version extends RangedInt {

  static final Version[] cache = new Version[128];

  private Version(int value, String fieldName) {
    super(value, fieldName, 0, true, null, null);
  }

  private static Version of(int value, String fieldName) {
    // 懒加载且不处理并发问题了，创建多个对象也问题不大。
    if (value >= 0 && value < 128) {
      return Optional.ofNullable(cache[value])
          .orElseGet(
              () -> {
                var version = new Version(value, fieldName);
                cache[value] = version;
                return version;
              });
    }
    return new Version(value, fieldName);
  }

  @JsonCreator
  public static Version from(int i) {
    return of(i, "Version");
  }

  public static Version valueOf(Integer i, String fieldName) {
    return of(parseInteger(i, fieldName), fieldName);
  }
}
