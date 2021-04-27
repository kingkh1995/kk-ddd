package com.kkk.op.support.types;

import lombok.EqualsAndHashCode;

/**
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true)
public class PageSize extends RangedLong {

    // todo... 改为可配置，并且是不同项目不同配置
    // 默认最大查询条数
    private final static transient Long MAX_SIZE = 1000L;

    protected PageSize(Long l, String fieldName) {
        super(l, fieldName, 1L, MAX_SIZE);
    }

    public static PageSize valueOf(Long l) {
        return valueOf(l, null);
    }

    public static PageSize valueOf(Long l, String fieldName) {
        return new PageSize(l, fieldName);
    }

}
