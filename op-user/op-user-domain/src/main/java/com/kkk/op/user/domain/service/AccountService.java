package com.kkk.op.user.domain.service;

import com.kkk.op.support.marker.EntityService;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.Account;
import javax.validation.constraints.NotNull;

/**
 * domain service
 *
 * @author KaiKoo
 */
public interface AccountService extends EntityService<Account, LongId> {

  /** 判断是否允许更新 */
  boolean allowModify(@NotNull Account oldAccount, @NotNull Account newAccount);
}
