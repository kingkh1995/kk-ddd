package com.kkk.op.user.domain.strategy;

import com.kkk.op.support.marker.AbstractStrategyManager;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.enums.AccountStatusEnum;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

/**
 * Strategy 不应该直接操作对象，而是通过返回计算后的值，在 Domain Service 里对对象进行操作
 *
 * @author KaiKoo
 */
@Component// 配合InitializingBean使用
public class AccountModifyStrategyManager extends AbstractStrategyManager {

    private final Map<AccountStatusEnum, AccountModifyStrategy> enumStrategyMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        // 构造map
        if (enumStrategyMap.isEmpty()) {
            applicationContext.getBeansOfType(AccountModifyStrategy.class)
            .forEach((s, accountModifyStrategy) -> enumStrategyMap
                            .put(accountModifyStrategy.getStatusEnum(), accountModifyStrategy));
        }
    }

    public boolean allowModify(@NotNull Account oldAccount, @NotNull Account newAccount) {
        var accountModifyStrategy = enumStrategyMap.get(oldAccount.getStatus().getValue());
        if (accountModifyStrategy != null) {
            return accountModifyStrategy.allowModify(oldAccount, newAccount);
        }
        return false;
    }


}
