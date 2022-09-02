package com.kk.ddd.user.application.factory;

import com.kk.ddd.support.model.command.AccountModifyCommand;
import com.kk.ddd.user.domain.entity.Account;
import com.kk.ddd.user.domain.type.AccountId;
import com.kk.ddd.user.domain.type.UserId;
import org.springframework.stereotype.Component;

/**
 * 用于创建领域对象，应当包含业务逻辑，因为返回的领域必须得是合法的。<br/>
 * 通过validate方法或Strategy实现。
 *
 * <br/>
 *
 * @author KaiKoo
 */
@Component
public class AccountFactory {

    public Account create(AccountModifyCommand command) {
        return Account.builder()
                .id(AccountId.valueOf(command.getId(), "accountId"))
                .userId(UserId.valueOf(command.getUserId(), "userId"))
                .build()
                .validate();
    }

}
