package com.kk.ddd.user.domain.strategy;

import com.kk.ddd.support.constant.AccountTypeEnum;
import com.kk.ddd.support.util.strategy.EStrategy;
import com.kk.ddd.user.domain.entity.Account;
import javax.validation.constraints.NotNull;

/**
 * Account策略类接口 <br>
 *
 * @author KaiKoo
 */
public interface AccountStrategy extends EStrategy<AccountTypeEnum> {

  /** 判断是否可以更新 */
  default boolean allowModify(@NotNull Account oldAccount, @NotNull Account newAccount) {
    return false; // 默认false
  }
}
