package com.kkk.op.user.domain.strategy.modify;

import com.kkk.op.support.enums.AccountStateEnum;
import com.kkk.op.user.domain.entity.Account;
import org.springframework.stereotype.Component;

/**
 * Init状态Account策略实现类 <br>
 *
 * @author KaiKoo
 */
@Component
public class InitAccountStrategy implements AccountStrategy {

  @Override
  public AccountStateEnum getIdentifier() {
    return AccountStateEnum.INIT;
  }

  @Override
  public boolean allowModify(Account oldAccount, Account newAccount) {
    // todo...
    return true;
  }
}
