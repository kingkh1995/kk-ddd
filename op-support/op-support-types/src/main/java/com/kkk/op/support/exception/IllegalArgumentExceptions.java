package com.kkk.op.support.exception;

/**
 *
 * @author KaiKoo
 */
public final class IllegalArgumentExceptions {

    private IllegalArgumentExceptions() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static IllegalArgumentException forNull(String fieldName) {
        return new IllegalArgumentException(getFieldName(fieldName) + "不能为空！");
    }

    public static IllegalArgumentException forWrongPattern(String fieldName) {
        return new IllegalArgumentException(getFieldName(fieldName) + "格式不正确！");
    }

    public static IllegalArgumentException forInvalidEnum(String fieldName) {
        return new IllegalArgumentException(getFieldName(fieldName) + "枚举值不合法！");
    }

    public static IllegalArgumentException forMaxValue(String fieldName, Number max,
            boolean included) {
        var sb = new StringBuilder(getFieldName(fieldName)).append("必须小于")
                .append(included ? "等于" : "").append(max);
        return new IllegalArgumentException(sb.toString());
    }

    public static IllegalArgumentException forMinValue(String fieldName, Number min,
            boolean included) {
        var sb = new StringBuilder(getFieldName(fieldName)).append("必须大于")
                .append(included ? "等于" : "").append(min);
        return new IllegalArgumentException(sb.toString());
    }

    private static String getFieldName(String fieldName) {
        return fieldName == null || fieldName.isBlank() ? "value" : fieldName;
    }

}
