package com.kk.ddd.user.query.service.impl;

import com.kk.ddd.support.annotation.QueryService;
import com.kk.ddd.user.domain.entity.Account;
import com.kk.ddd.user.domain.type.AccountId;
import com.kk.ddd.user.query.service.AccountQueryService;
import com.kk.ddd.user.repository.AccountRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Slf4j
@QueryService // 标记为query service
@RequiredArgsConstructor
public class AccountQueryServiceImpl implements AccountQueryService {

  private final AccountRepository accountRepository;

  @Override
  public Optional<Account> find(AccountId accountId) {
    return accountRepository.find(accountId);
  }

  @Override
  public List<Account> find(Set<AccountId> accountIds) {
    return accountRepository.find(accountIds);
  }
}
