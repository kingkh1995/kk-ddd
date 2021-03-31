package com.kkk.op.support.types;

import com.kkk.op.support.marker.Identifier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * long类型Id
 * @author KaiKoo
 */
@ToString
@EqualsAndHashCode
public class LongId implements Identifier {

    @Getter
    private final Long id;

    public LongId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id不能为空");
        }
        if (id < 1) {
            throw new IllegalArgumentException("id必须大于0");
        }
        this.id = id;
    }

    @Override
    public String getValue() {
        return this.id.toString();
    }
}
