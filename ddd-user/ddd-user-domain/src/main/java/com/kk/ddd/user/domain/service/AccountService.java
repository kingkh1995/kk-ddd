package com.kk.ddd.user.domain.service;

import com.kk.ddd.support.marker.EntityService;
import com.kk.ddd.user.domain.entity.Account;
import com.kk.ddd.user.domain.type.AccountId;
import javax.validation.constraints.NotNull;

/**
 * domain service
 *
 * @author KaiKoo
 */
public interface AccountService extends EntityService<Account, AccountId> {

  /** 判断是否允许更新 */
  boolean allowModify(@NotNull Account account);
}
