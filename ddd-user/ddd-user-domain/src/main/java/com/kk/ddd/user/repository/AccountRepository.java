package com.kk.ddd.user.repository;

import com.kk.ddd.support.marker.EntityRepository;
import com.kk.ddd.user.domain.entity.Account;
import com.kk.ddd.user.domain.type.AccountId;
import com.kk.ddd.user.domain.type.UserId;
import java.util.List;
import javax.validation.constraints.NotNull;

/**
 * <br>
 *
 * @author KaiKoo
 */
public interface AccountRepository extends EntityRepository<Account, AccountId> {

  List<Account> find(@NotNull UserId userId);
}
