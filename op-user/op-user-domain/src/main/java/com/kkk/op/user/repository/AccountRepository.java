package com.kkk.op.user.repository;

import com.kkk.op.support.marker.EntityRepository;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.type.AccountId;
import com.kkk.op.user.domain.type.UserId;
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
