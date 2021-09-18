package com.kkk.op.user.domain.service.impl;

import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.service.AccountService;
import com.kkk.op.user.domain.strategy.AccountStrategyManager;
import com.kkk.op.user.domain.types.AccountId;
import com.kkk.op.user.repository.AccountRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 1.查询类方法在专门的 query service中实现，为了不至于太复杂，直接放在 domain service 中 <br>
 * 2.实体的行为仅涉及自身的状态变更，业务逻辑在实体类中 <br>
 * 3.多个实体的状态变更的行为业务逻辑在 domain service 中 <br>
 * 4.该类只做组件调用，参数的合法性均放置到对应的实体类总 <br>
 *
 * @author KaiKoo
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;

  private final AccountStrategyManager accountStrategyManager;

  @Override
  public Optional<Account> find(@NotNull AccountId id) {
    return accountRepository.find(id);
  }

  @Override
  public void remove(@NotNull Account account) {
    accountRepository.remove(account);
  }

  @Override
  public void save(@NotNull Account account) {
    accountRepository.save(account);
  }

  @Override
  public List<Account> list(@NotEmpty Set<AccountId> accountIds) {
    return accountRepository.list(accountIds);
  }

  @Override
  public boolean allowModify(@NotNull Account oldAccount, @NotNull Account newAccount) {
    return accountStrategyManager.allowModify(oldAccount, newAccount);
  }
}
