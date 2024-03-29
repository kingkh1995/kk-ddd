package com.kk.ddd.support.util;

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

  public static IllegalArgumentException forIsBlank(String fieldName) {
    return new IllegalArgumentException(fieldName + "不能为空字符串");
  }

  public static IllegalArgumentException forWrongPattern(String fieldName) {
    return new IllegalArgumentException(fieldName + "格式不正确");
  }

  public static IllegalArgumentException forWrongClass(String fieldName) {
    return new IllegalArgumentException(fieldName + "类型不正确");
  }

  public static IllegalArgumentException forInvalidEnum(String fieldName) {
    return new IllegalArgumentException(fieldName + "枚举值不合法");
  }

  public static IllegalArgumentException forMaxValue(
      String fieldName, Number max, boolean inclusive) {
    var message = fieldName + "必须小于" + (inclusive ? "等于" : "") + max;
    return new IllegalArgumentException(message);
  }

  public static IllegalArgumentException forMinValue(
      String fieldName, Number min, boolean inclusive) {
    var message = fieldName + "必须大于" + (inclusive ? "等于" : "") + min;
    return new IllegalArgumentException(message);
  }

  public static IllegalArgumentException forMaxLength(
      String fieldName, Number max, boolean inclusive) {
    var message = fieldName + "的长度必须小于" + (inclusive ? "等于" : "") + max;
    return new IllegalArgumentException(message);
  }

  public static IllegalArgumentException forScaleAbove(String fieldName, int scale) {
    var message = new StringBuilder(fieldName);
    if (scale == 0) {
      message.append("必须为整数");
    } else if (scale > 0) {
      message.append("最多只能保留").append(scale).append("位小数");
    } else {
      message.append("整数位最后").append(-scale).append("位必须为0且不能含有小数位");
    }
    return new IllegalArgumentException(message.toString());
  }

  public static IllegalArgumentException requireAfter(
      String fieldName, boolean includePresent, boolean obtainTime) {
    var message = fieldName + (includePresent ? "不能早于" : "必须晚于") + (obtainTime ? "当前时间" : "今天");
    return new IllegalArgumentException(message);
  }

  public static IllegalArgumentException requireBefore(
      String fieldName, boolean includePresent, boolean obtainTime) {
    var message = fieldName + (includePresent ? "不能晚于" : "必须早于") + (obtainTime ? "当前时间" : "今天");
    return new IllegalArgumentException(message);
  }
}
