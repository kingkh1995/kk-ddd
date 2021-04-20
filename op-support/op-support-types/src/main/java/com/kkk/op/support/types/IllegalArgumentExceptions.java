package com.kkk.op.support.types;

/**
 *
 * @author KaiKoo
 */
public final class IllegalArgumentExceptions {

    private IllegalArgumentExceptions() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static IllegalArgumentException forNull(String prefix) {
        return new IllegalArgumentException(getPrefix(prefix) + "不能为空！");
    }

    public static IllegalArgumentException forWrongPattern(String prefix) {
        return new IllegalArgumentException(getPrefix(prefix) + "格式不正确！");
    }

    public static IllegalArgumentException forInvalidValue(String prefix) {
        return new IllegalArgumentException(getPrefix(prefix) + "值不合法！");
    }

    public static IllegalArgumentException forMaxValue(String prefix, Number max,
            boolean included) {
        return new IllegalArgumentException(
                String.format("%s必须小于%s%s！", getPrefix(prefix), included ? "等于" : "", max));
    }

    public static IllegalArgumentException forMinValue(String prefix, Number min,
            boolean included) {
        return new IllegalArgumentException(
                String.format("%s必须大于%s%s！", getPrefix(prefix), included ? "等于" : "", min));
    }

    public static String getPrefix(String prefix) {
        return prefix == null || prefix.isBlank() ? "value" : prefix;
    }

}
