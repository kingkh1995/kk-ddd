package com.kkk.op.user.domain.strategy;

import com.kkk.op.support.bean.AbstractStrategyManager;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.enums.AccountStatusEnum;
import java.util.EnumMap;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

/**
 * Strategy 不应该直接操作对象，而是通过返回计算后的值，在 Domain Service 里对对象进行操作
 * todo... 待优化
 * @author KaiKoo
 */
@Component// 配合InitializingBean使用
public class AccountModifyStrategyManager extends AbstractStrategyManager {

    // 使用 EnumMap
    private final transient EnumMap<AccountStatusEnum, AccountModifyStrategy> modifyStrategyMap = new EnumMap<>(
            AccountStatusEnum.class);

    @Override
    public void afterPropertiesSet() {
        // 构造map
        if (modifyStrategyMap.isEmpty()) {
            applicationContext.getBeansOfType(AccountModifyStrategy.class)
                    .forEach((s, accountModifyStrategy) -> modifyStrategyMap
                            .put(accountModifyStrategy.getStatusEnum(), accountModifyStrategy));
        }
    }

    public boolean allowModify(@NotNull Account oldAccount, @NotNull Account newAccount) {
        var accountModifyStrategy = modifyStrategyMap.get(oldAccount.getStatus().getValue());
        if (accountModifyStrategy != null) {
            return accountModifyStrategy.allowModify(oldAccount, newAccount);
        }
        return false;
    }

}
