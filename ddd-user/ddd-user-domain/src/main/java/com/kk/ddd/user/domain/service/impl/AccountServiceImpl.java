package com.kk.ddd.user.domain.service.impl;

import com.kk.ddd.user.domain.entity.Account;
import com.kk.ddd.user.domain.service.AccountService;
import com.kk.ddd.user.domain.strategy.AccountStrategyManager;
import com.kk.ddd.user.repository.AccountRepository;
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
  public void save(@NotNull Account account) {
    accountRepository.save(account);
  }

  @Override
  public void remove(@NotNull Account account) {
    accountRepository.remove(account);
  }

  @Override
  public boolean allowModify(@NotNull Account account) {
    return accountStrategyManager.allowModify(
        accountRepository.find(account.getId()).get(), account);
  }
}
