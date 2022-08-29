package com.kk.ddd.support.util;

import java.math.BigDecimal;

/**
 *
 * <br/>
 *
 * @author KaiKoo
 */
public final class ValidateUtils {

    private ValidateUtils() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static void minValue(int value, int min, boolean minInclusive, String fieldName) {
        var cmp = Integer.compare(value, min);
        if ((minInclusive && cmp < 0) || (!minInclusive && cmp <= 0)) {
            throw IllegalArgumentExceptions.forMinValue(fieldName, min, minInclusive);
        }
    }

    public static void maxValue(int value, int max, boolean maxInclusive, String fieldName) {
        var cmp = Integer.compare(value, max);
        if ((maxInclusive && cmp > 0) || (!maxInclusive && cmp >= 0)) {
            throw IllegalArgumentExceptions.forMaxValue(fieldName, max, maxInclusive);
        }
    }

    public static void minValue(long value, long min, boolean minInclusive, String fieldName) {
        var cmp = Long.compare(value, min);
        if ((minInclusive && cmp < 0) || (!minInclusive && cmp <= 0)) {
            throw IllegalArgumentExceptions.forMinValue(fieldName, min, minInclusive);
        }
    }

    public static void maxValue(long value, long max, boolean maxInclusive, String fieldName) {
        var cmp = Long.compare(value, max);
        if ((maxInclusive && cmp > 0) || (!maxInclusive && cmp >= 0)) {
            throw IllegalArgumentExceptions.forMaxValue(fieldName, max, maxInclusive);
        }
    }

    public static void minValue(BigDecimal value, BigDecimal min, boolean minInclusive, String fieldName) {
        var cmp = value.compareTo(min);
        if ((minInclusive && cmp < 0) || (!minInclusive && cmp <= 0)) {
            throw IllegalArgumentExceptions.forMinValue(fieldName, min, minInclusive);
        }
    }

    public static void maxValue(BigDecimal value, BigDecimal max, boolean maxInclusive, String fieldName) {
        var cmp = value.compareTo(max);
        if ((maxInclusive && cmp > 0) || (!maxInclusive && cmp >= 0)) {
            throw IllegalArgumentExceptions.forMaxValue(fieldName, max, maxInclusive);
        }
    }

    public static void scaleAbove(BigDecimal value, int scale, String fieldName){
        // 去除尾部的0
        if (value.stripTrailingZeros().scale() > scale) {
            throw IllegalArgumentExceptions.forScaleAbove(fieldName, scale);
        }
    }

}
