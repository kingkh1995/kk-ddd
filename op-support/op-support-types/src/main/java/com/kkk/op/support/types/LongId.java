package com.kkk.op.support.types;

import com.kkk.op.support.marker.Identifier;

/**
 * long类型Id
 *
 * @author KaiKoo
 */
public class LongId extends RangedLong implements Identifier {

    protected LongId(Long l, String prefix) {
        super(l, prefix, 1L, null);
    }

    /**
     * 不对外提供构造函数，只提供 valueOf 静态方法
     */

    public static LongId valueOf(Long l) {
        return valueOf(l, null);
    }

    public static LongId valueOf(Long l, String prefix) {
        return new LongId(l, prefix);
    }

    public static LongId valueOf(String s) {
        return valueOf(s, null);
    }

    public static LongId valueOf(String s, String prefix) {
        return new LongId(parseLong(s, prefix), prefix);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LongId) { // instanceof关键字如果obj为null会直接返回false
            return this.value == ((LongId) obj).value;
        }
        return false;
    }

    @Override
    public String stringValue() {
        return Long.toString(this.value);
    }

}

