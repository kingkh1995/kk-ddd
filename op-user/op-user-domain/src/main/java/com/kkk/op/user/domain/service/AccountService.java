package com.kkk.op.user.domain.service;

import com.kkk.op.support.marker.EntityService;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.type.AccountId;
import javax.validation.constraints.NotNull;

/**
 * domain service
 *
 * @author KaiKoo
 */
public interface AccountService extends EntityService<Account, AccountId> {

  /** 判断是否允许更新 */
  boolean allowModify(@NotNull Account oldAccount, @NotNull Account newAccount);
}
