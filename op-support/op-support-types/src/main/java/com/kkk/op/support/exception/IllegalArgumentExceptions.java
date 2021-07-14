package com.kkk.op.support.exception;

/**
 * 参数异常工具类 <br>
 * todo... 待设计
 *
 * @author KaiKoo
 */
public final class IllegalArgumentExceptions {

  private IllegalArgumentExceptions() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  public static IllegalArgumentException forIsNull(String fieldName) {
    return new IllegalArgumentException(fieldName + "不能为空");
  }

  public static IllegalArgumentException forWrongPattern(String fieldName) {
    return new IllegalArgumentException(fieldName + "格式不正确");
  }

  public static IllegalArgumentException forMustNumber(String fieldName) {
    return new IllegalArgumentException(fieldName + "必须为数字");
  }

  public static IllegalArgumentException forInvalidEnum(String fieldName) {
    return new IllegalArgumentException(fieldName + "枚举值不合法");
  }

  public static IllegalArgumentException forMaxValue(
      String fieldName, Number max, boolean included) {
    var message =
        new StringBuilder(fieldName)
            .append("必须小于")
            .append(included ? "等于" : "")
            .append(max)
            .toString();
    return new IllegalArgumentException(message);
  }

  public static IllegalArgumentException forMinValue(
      String fieldName, Number min, boolean included) {
    var message =
        new StringBuilder(fieldName)
            .append("必须大于")
            .append(included ? "等于" : "")
            .append(min)
            .toString();
    return new IllegalArgumentException(message);
  }

  public static IllegalArgumentException forAtMostScale(String fieldName, int scale) {
    var message = fieldName + (scale == 0 ? "必须为整数" : "只能保留" + scale + "位小数");
    return new IllegalArgumentException(message);
  }
}
