package com.kkk.op.support.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kkk.op.support.marker.Identifier;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * long类型Id
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true) // 重写EqualsAndHashCode
public class LongId extends SpecificNumber implements Identifier {

  protected LongId(@NotNull BigDecimal value, String fieldName) {
    super(value, fieldName, ZERO, false, null, null, 0);
  }

  /**
   * 内部实现提供私有的 of 静态方法 <br>
   * 不对外提供构造函数，只提供 valueOf（不可靠输入） 和 from（可靠输入） 静态方法 <br>
   */
  private static LongId of(@NotNull BigDecimal value, String fieldName) {
    return new LongId(value, fieldName);
  }

  // 针对可靠输入的 from 方法
  @JsonCreator // 自定义Jackson反序列化，可以用于构造方法和静态工厂方法，使用@JsonProperty注释字段
  public static LongId from(@NotNull Number id) {
    return of(new BigDecimal(id.toString()), "LongId");
  }

  // 针对不可靠输入的 valueOf 方法
  public static LongId valueOf(Long l, String fieldName) {
    return of(parse(l, fieldName), fieldName);
  }

  public static LongId valueOf(String s, String fieldName) {
    return of(parse(s, fieldName), fieldName);
  }

  @Override
  public String stringValue() {
    return super.value().toString();
  }

  @Override
  @JsonValue(false) // 需要覆盖父类的注释，不然会报错重复定义
  public BigDecimal value() {
    return super.value();
  }

  @JsonValue // 子类自定义序列化
  public long getValue() {
    return super.longValue();
  }
}
