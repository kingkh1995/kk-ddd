package com.kkk.op.support.interfaces;

import javax.validation.constraints.NotNull;

/**
 * 实体类Repository
 * @author KaiKoo
 */
public interface EntityRepository<T extends Entity, ID extends Identifier> {

    /**
     * 通过 ID 寻找 Entity。
     */
    T find(@NotNull ID id);

    /**
     * 将一个 Entity 从 Repository 移除
     */
    void remove(@NotNull T entity);

    /**
     * 保存一个 Entity
     */
    ID save(@NotNull T entity);

}
