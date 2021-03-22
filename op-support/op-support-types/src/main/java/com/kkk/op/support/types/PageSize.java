package com.kkk.op.support.types;

import com.kkk.op.support.marker.Type;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 * @author KaiKoo
 */
@ToString
@EqualsAndHashCode
public class PageSize implements Type {

    private final Long size;

    // todo... 改为可配置，并且是不同项目不同配置
    // 默认最大查询条数
    private final static transient Long MAX_SIZE = 1000L;

    public PageSize(Long size) {
        if (size == null) {
            throw new IllegalArgumentException("size不能为空");
        }
        if (size < 1) {
            throw new IllegalArgumentException("size必须大于0");
        }
        if (size > MAX_SIZE) {
            throw new IllegalArgumentException(String.format("size最大值不能超过%d", MAX_SIZE));
        }
        this.size = size;
    }

    public Long getValue() {
        return this.size;
    }

}
