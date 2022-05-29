package com.kkk.op.user.query.service.impl;

import com.kkk.op.support.annotation.QueryService;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.type.AccountId;
import com.kkk.op.user.query.service.AccountQueryService;
import com.kkk.op.user.repository.AccountRepository;
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
