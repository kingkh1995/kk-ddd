package com.kkk.op.support.models.base;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * 查询类DTO基类（默认包含分页参数）
 * @author KaiKoo
 */
public abstract class AbstractQueryDTO implements Serializable {

    @Getter
    @Setter
    protected Long size = 10L;

    @Getter
    @Setter
    protected Long current = 1L;

}
