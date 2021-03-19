package com.kkk.op.user.domain.strategy;

import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.enums.AccountStatusEnum;
import javax.validation.constraints.NotNull;

/**
 *
 * @author KaiKoo
 */
public interface AccountModifyStrategy {

    /**
     * 获取该策略对应的枚举
     */
    AccountStatusEnum getStatusEnum();

    /**
     * 判断是否可以更新
     */
    boolean allowModify(@NotNull Account oldAccount, @NotNull Account newAccount);

}
