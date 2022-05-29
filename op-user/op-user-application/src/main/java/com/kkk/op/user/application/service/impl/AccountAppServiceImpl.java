package com.kkk.op.user.application.service.impl;

import com.kkk.op.support.model.command.AccountModifyCommand;
import com.kkk.op.support.model.dto.AccountDTO;
import com.kkk.op.user.application.service.AccountAppService;
import com.kkk.op.user.assembler.AccountAssembler;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.service.AccountService;
import com.kkk.op.user.domain.type.AccountId;
import com.kkk.op.user.domain.type.UserId;
import com.kkk.op.user.query.service.AccountQueryService;
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

  private final AccountService accountService;

  private final AccountQueryService accountQueryService;

  @Override
  public long createAccount(AccountModifyCommand createCommand) {
    // 转换对象
    var account =
        Account.builder().userId(UserId.valueOf(createCommand.getUserId(), "userId")).build();
    // 行为发生
    account.save(accountService);
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
    // 行为发生
    account.save(accountService);
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
    // 行为发生
    optional.get().remove(accountService);
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
