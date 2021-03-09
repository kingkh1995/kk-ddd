package com.kkk.op.support.type;

import com.kkk.op.support.marker.Identifier;
import javax.validation.ValidationException;

/**
 * long类型Id
 * @author KaiKoo
 */
public class LongId implements Identifier {

    private final Long id;

    public LongId(Long id) {
        if (id == null) {
            throw new ValidationException("id不能为空");
        }
        if (id <= 0) {
            throw new ValidationException("id格式不合法");
        }
        this.id = id;
    }

    public Long getValue() {
        return this.id;
    }
}
