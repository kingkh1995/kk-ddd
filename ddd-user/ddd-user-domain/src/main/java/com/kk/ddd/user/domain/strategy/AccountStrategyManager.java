package com.kk.ddd.user.domain.strategy;

import com.kk.ddd.support.enums.AccountTypeEnum;
import com.kk.ddd.support.util.strategy.AbstractEStrategyManager;
import com.kk.ddd.user.domain.entity.Account;
import java.util.EnumSet;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

/**
 * Account策略类工厂类 <br>
 *
 * @author KaiKoo
 */
@Component
public class AccountStrategyManager
    extends AbstractEStrategyManager<AccountTypeEnum, AccountStrategy> {

  public AccountStrategyManager() {
    // 设置收集方案
    super(EnumSet.allOf(CollectTactic.class));
  }

  public boolean allowModify(@NotNull Account oldAccount, @NotNull Account newAccount) {
    return super.getSingleton(oldAccount.getType().toEnum()).allowModify(oldAccount, newAccount);
  }
}
