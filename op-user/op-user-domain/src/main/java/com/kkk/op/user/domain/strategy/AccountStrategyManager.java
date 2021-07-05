package com.kkk.op.user.domain.strategy;

import com.kkk.op.support.base.AbstractStrategyManager;
import com.kkk.op.support.enums.AccountStatusEnum;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.strategy.modify.AccountModifyStrategy;
import java.util.EnumMap;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

/**
 * 策略类不应该直接操作对象，而是通过返回计算后的值，在 Domain Service 里对对象进行操作 <br>
 * todo... 待优化
 *
 * @author KaiKoo
 */
@Component // 配合InitializingBean使用
public class AccountStrategyManager extends AbstractStrategyManager {

  // Modify Strategy  使用EnumMap
  private final transient EnumMap<AccountStatusEnum, AccountModifyStrategy> modifyStrategyMap =
      new EnumMap<>(AccountStatusEnum.class);

  @Override
  public void afterPropertiesSet() {
    if (!this.modifyStrategyMap.isEmpty()) {
      return;
    }
    // 构造map
    this.applicationContext
        .getBeansOfType(AccountModifyStrategy.class)
        .forEach(
            (s, accountModifyStrategy) ->
                this.modifyStrategyMap.put(
                    accountModifyStrategy.getStatusEnum(), accountModifyStrategy));
  }

  public boolean allowModify(@NotNull Account oldAccount, @NotNull Account newAccount) {
    var accountModifyStrategy = this.modifyStrategyMap.get(oldAccount.getStatus().getValue());
    if (accountModifyStrategy != null) {
      return accountModifyStrategy.allowModify(oldAccount, newAccount);
    }
    return false;
  }
}
