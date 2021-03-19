package com.kkk.op.support.bean;

import com.kkk.op.support.types.PageSize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 查询类实体基类
 * @author KaiKoo
 */
@EqualsAndHashCode
@ToString
@Getter
@SuperBuilder//父类和子类都需要加上该注解
public abstract class AbstractQuery {

    private PageSize size;

    // todo... 当前页 current(postive long)

}
