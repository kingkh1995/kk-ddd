package com.kk.ddd.user.application.service.impl;

import com.kk.ddd.support.model.command.AccountModifyCommand;
import com.kk.ddd.support.model.dto.AccountDTO;
import com.kk.ddd.user.application.assembler.AccountAssembler;
import com.kk.ddd.user.application.factory.AccountFactory;
import com.kk.ddd.user.application.service.AccountAppService;
import com.kk.ddd.user.domain.entity.Account;
import com.kk.ddd.user.domain.type.AccountId;
import com.kk.ddd.user.domain.type.UserId;
import com.kk.ddd.user.query.service.AccountQueryService;
import com.kk.ddd.user.repository.AccountRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Service
@RequiredArgsConstructor
public class AccountAppServiceImpl implements AccountAppService {

  private final AccountAssembler accountAssembler = AccountAssembler.INSTANCE;

  private final AccountRepository accountRepository;

  private final AccountQueryService accountQueryService;

  private final AccountFactory accountFactory;

  @Override
  public long createAccount(AccountModifyCommand createCommand) {
    // 转换对象
    var account = accountFactory.create(createCommand);
    // 持久化
    accountRepository.save(account);
    // todo... 发送事件

    // 返回id
    return account.getId().getValue();
  }

  @Override
  public void updateAccount(AccountModifyCommand updateCommand) {
    // 转换对象
    var account =
        Account.builder()
            .id(AccountId.valueOf(updateCommand.getId(), "id"))
            .userId(UserId.valueOf(updateCommand.getUserId(), "userId"))
            .build();
    // 持久化
    accountRepository.save(account);
    // todo... 发送事件

  }

  @Override
  public void deleteAccount(Long userId, Long accountId) {
    // 查询领域
    var optional = accountQueryService.find(AccountId.valueOf(accountId, "id"));
    // 编排逻辑
    if (optional.isEmpty()) {
      return;
    }
    // 持久化
    accountRepository.remove(optional.get());
    // todo... 发送事件

  }

  @Override
  public AccountDTO queryAccount(Long userId, Long accountId) {
    // todo...
    return accountQueryService
        .find(AccountId.valueOf(accountId, "id"))
        .map(accountAssembler::toDTO)
        .get();
  }

  @Override
  public List<AccountDTO> queryAccounts(Long userId) {
    // todo... 实现

    return null;
  }
}
