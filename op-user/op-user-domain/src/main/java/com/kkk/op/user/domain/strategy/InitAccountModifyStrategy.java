package com.kkk.op.user.domain.strategy;

import com.kkk.op.support.enums.AccountStatusEnum;
import com.kkk.op.user.domain.entity.Account;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

/**
 *
 * @author KaiKoo
 */
@Component//加上注解才能被ApplicationContext获取到
public class InitAccountModifyStrategy implements AccountModifyStrategy {

    @Override
    public AccountStatusEnum getStatusEnum() {
        return AccountStatusEnum.INIT;
    }

    @Override
    public boolean allowModify(@NotNull Account oldAccount, @NotNull Account newAccount) {
        // todo... 待实现
        return true;
    }
}
