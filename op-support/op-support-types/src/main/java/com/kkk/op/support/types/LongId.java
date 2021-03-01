package com.kkk.op.support.types;

import com.kkk.op.support.interfaces.Identifier;

/**
 *
 * @author KaiKoo
 */
public class LongId implements Identifier {

    private final Long id;

    public LongId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id不能为空");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("id格式不合法");
        }
        this.id = id;
    }

    public Long getValue() {
        return id;
    }
}
