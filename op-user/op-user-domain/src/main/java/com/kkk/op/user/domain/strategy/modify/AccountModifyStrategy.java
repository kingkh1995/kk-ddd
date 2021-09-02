package com.kkk.op.user.domain.strategy.modify;

import com.kkk.op.support.enums.AccountStateEnum;
import com.kkk.op.support.marker.Strategy;
import com.kkk.op.user.domain.entity.Account;
import javax.validation.constraints.NotNull;

/**
 * 账户更新策略类接口 <br>
 *
 * @author KaiKoo
 */
public interface AccountModifyStrategy extends Strategy<AccountStateEnum> {

  /** 判断是否可以更新 */
  default boolean allowModify(@NotNull Account oldAccount, @NotNull Account newAccount) {
    return false; // 默认false
  }
}
