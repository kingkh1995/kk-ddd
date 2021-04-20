package com.kkk.op.support.types;

import com.kkk.op.support.exception.IllegalArgumentExceptions;
import com.kkk.op.support.marker.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
public abstract class RangedLong implements Type {

    @Getter // 设置为protected以便子类使用，使用基本数据类型
    protected long value;

    /**
     * 默认含头不含尾
     * @param l 可以为Null
     * @param prefix 异常抛出前缀，默认为value
     * @param min 最小值（包含）
     * @param max 最大值（不包含）
     */
    protected RangedLong(Long l, String prefix, Long min, Long max) {
        if (l == null) {
            throw IllegalArgumentExceptions.forNull(prefix);
        }
        if (min != null && l < min) {
            throw IllegalArgumentExceptions.forMinValue(prefix, min, true);
        }
        if (max != null && l >= max) {
            throw IllegalArgumentExceptions.forMaxValue(prefix, max, false);
        }
        this.value = l;
    }

    protected static long parseLong(String s, String prefix) {
        if (s == null || s.isBlank()) {
            throw IllegalArgumentExceptions.forNull(prefix);
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw IllegalArgumentExceptions.forWrongPattern(prefix);
        }
    }

}
