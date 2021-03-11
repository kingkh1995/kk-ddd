package com.kkk.op.support.types;

import com.kkk.op.support.marker.Identifier;
import javax.validation.ValidationException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * long类型Id
 * @author KaiKoo
 */
@ToString
@EqualsAndHashCode
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
