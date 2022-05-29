package com.kkk.op.user.domain.strategy;

import com.kkk.op.support.enums.AccountStateEnum;
import com.kkk.op.support.util.strategy.AbstractEStrategyManager;
import com.kkk.op.user.domain.entity.Account;
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
    extends AbstractEStrategyManager<AccountStateEnum, AccountStrategy> {

  public AccountStrategyManager() {
    // 设置收集方案
    super(EnumSet.allOf(CollectTactic.class));
  }

  public boolean allowModify(@NotNull Account oldAccount, @NotNull Account newAccount) {
    return super.getSingleton(oldAccount.getState().getValue()).allowModify(oldAccount, newAccount);
  }
}
