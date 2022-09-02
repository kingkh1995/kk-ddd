package com.kk.ddd.support.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kk.ddd.support.core.Identifier;
import com.kk.ddd.support.util.ParseUtils;
import com.kk.ddd.support.util.ValidateUtils;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * long类型Id
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class LongId implements Identifier, Comparable<LongId> {

  private final long value;

  /**
   * 内部实现提供私有的静态方法供其他方法使用，仅该私有静态方法能调用构造方法。 <br>
   * 不对外提供构造函数，只提供 valueOf（不可靠输入） 和 of（可靠输入） 静态方法。 <br>
   */
  private static LongId of(final long value, final String fieldName) {
    ValidateUtils.minValue(value, 0, false, fieldName);
    return new LongId(value);
  }

  // 针对可靠输入的 of 方法
  @JsonCreator // 自定义Jackson反序列化，可以用于构造方法和静态工厂方法，使用@JsonProperty注释字段
  public static LongId of(final long l) {
    return of(l, "LongId");
  }

  // 针对不可靠输入的 valueOf 方法
  public static LongId valueOf(final Object o, final String fieldName) {
    return of(ParseUtils.parseLong(o,fieldName), fieldName);
  }

  @Override
  public String identifier() {
    return String.valueOf(this.getValue());
  }

  @Override
  public int compareTo(LongId o) {
    return Long.compare(this.getValue(), o.getValue());
  }

  // 自定义Jackson序列化，也可以注释到字段，但是最好不要，因为无法被子类覆盖。
  @JsonValue // 如果父类也存在注解，需要声明 @JsonValue(false) 覆盖父类，不然会报错重复定义。
  public long getValue() {
    return this.value;
  }
}
