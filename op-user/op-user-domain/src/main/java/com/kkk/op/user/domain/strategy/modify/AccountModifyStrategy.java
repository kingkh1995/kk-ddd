package com.kkk.op.user.domain.strategy.modify;

import com.kkk.op.support.enums.AccountStatusEnum;
import com.kkk.op.user.domain.entity.Account;
import javax.validation.constraints.NotNull;

/**
 * <br>
 *
 * @author KaiKoo
 */
public interface AccountModifyStrategy {

  /** 获取该策略对应的枚举 */
  AccountStatusEnum getStatusEnum();

  /** 判断是否可以更新 */
  default boolean allowModify(@NotNull Account oldAccount, @NotNull Account newAccount) {
    return false; // 默认false
  }
}
