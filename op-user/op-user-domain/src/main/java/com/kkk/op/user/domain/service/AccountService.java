package com.kkk.op.user.domain.service;

import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.Account;
import javax.validation.constraints.NotNull;

/**
 * domain service
 *
 * @author KaiKoo
 */
public interface AccountService {

    /**
     * 通过 ID 查找
     */
    Account find(@NotNull LongId id);

    /**
     * 移除
     */
    void remove(@NotNull Account entity);

    /**
     * 保存
     */
    LongId save(@NotNull Account entity);

    /**
     *
     * 判断是否允许更新
     */
    boolean allowModify(@NotNull Account oldAccount, @NotNull Account newAccount);

}
