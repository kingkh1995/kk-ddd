package com.kkk.op.user.domain.strategy;

import com.kkk.op.support.base.AbstractStrategyManager;
import com.kkk.op.support.enums.AccountStateEnum;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.strategy.modify.AccountModifyStrategy;
import java.util.EnumSet;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

/**
 * AccountModifyStrategy策略类工厂类 <br>
 *
 * @author KaiKoo
 */
@Component
public class AccountStrategyManager
    extends AbstractStrategyManager<AccountStateEnum, AccountModifyStrategy> {

  public AccountStrategyManager() {
    // 设置收集方案
    super(EnumSet.allOf(CollectTactic.class));
  }

  public boolean allowModify(@NotNull Account oldAccount, @NotNull Account newAccount) {
    return Objects.requireNonNull(super.getSingleton(oldAccount.getState().getValue()))
        .allowModify(oldAccount, newAccount);
  }
}
