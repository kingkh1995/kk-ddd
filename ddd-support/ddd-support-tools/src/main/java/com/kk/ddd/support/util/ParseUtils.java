package com.kk.ddd.support.util;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * <br/>
 *
 * @author KaiKoo
 */
public final class ParseUtils {

    private ParseUtils() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static int parseInt(Object o, String fieldName) {
        if (o == null) {
            throw IllegalArgumentExceptions.forIsNull(fieldName);
        } else if (o instanceof Integer i) {
            return i;
        } else if (o instanceof String s) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                throw IllegalArgumentExceptions.forWrongPattern(fieldName);
            }
        }
        throw IllegalArgumentExceptions.forWrongClass(fieldName);
    }

    public static long parseLong(Object o, String fieldName) {
        if (o == null) {
            throw IllegalArgumentExceptions.forIsNull(fieldName);
        } else if (o instanceof Long l) {
            return l;
        } else if (o instanceof String s) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                throw IllegalArgumentExceptions.forWrongPattern(fieldName);
            }
        }
        throw IllegalArgumentExceptions.forWrongClass(fieldName);
    }

    public static BigDecimal parseBigDecimal(Object o, String fieldName) {
        if (o == null) {
            throw IllegalArgumentExceptions.forIsNull(fieldName);
        } else if (o instanceof BigDecimal v) {
            return v;
        } else if (o instanceof BigInteger v) {
            return new BigDecimal(v);
        } else if (o instanceof Byte || o instanceof Short || o instanceof Integer || o instanceof Long) {
            return BigDecimal.valueOf((long) o);
        } else if (o instanceof Number || o instanceof String) {
            try {
                return new BigDecimal(o.toString());
            } catch (NumberFormatException e) {
                throw IllegalArgumentExceptions.forWrongPattern(fieldName);
            }
        }
        throw IllegalArgumentExceptions.forWrongClass(fieldName);
    }

    public static <E extends Enum<E>> E parseEnum(Class<E> enumClass,  Object o, String fieldName) {
        if (o == null) {
            throw IllegalArgumentExceptions.forIsNull(fieldName);
        } else if (o instanceof String s) {
            try {
                // 如果不存在对应枚举，valueOf方法不会返回 null，而是抛出异常
                return Enum.valueOf(enumClass, s);
            } catch (IllegalArgumentException e) {
                throw IllegalArgumentExceptions.forInvalidEnum(fieldName);
            }
        } else if (enumClass.isInstance(o)) {
            return (E) o;
        }
        throw IllegalArgumentExceptions.forWrongClass(fieldName);
    }
}
