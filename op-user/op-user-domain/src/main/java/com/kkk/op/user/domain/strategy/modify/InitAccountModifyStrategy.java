package com.kkk.op.user.domain.strategy.modify;

import com.kkk.op.support.enums.AccountStatusEnum;
import com.kkk.op.user.domain.entity.Account;
import org.springframework.stereotype.Component;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Component
public class InitAccountModifyStrategy implements AccountModifyStrategy {

  @Override
  public AccountStatusEnum getStrategyID() {
    return AccountStatusEnum.INIT;
  }

  @Override
  public boolean allowModify(Account oldAccount, Account newAccount) {
    // todo...
    return false;
  }
}
