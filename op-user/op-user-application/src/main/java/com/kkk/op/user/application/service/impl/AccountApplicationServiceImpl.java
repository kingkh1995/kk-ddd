package com.kkk.op.user.application.service.impl;

import com.kkk.op.support.model.command.AccountModifyCommand;
import com.kkk.op.support.model.dto.AccountDTO;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.application.service.AccountApplicationService;
import com.kkk.op.user.assembler.AccountDTOAssembler;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.service.AccountService;
import com.kkk.op.user.domain.types.AccountId;
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
public class AccountApplicationServiceImpl implements AccountApplicationService {

  private final AccountDTOAssembler accountDTOAssembler = AccountDTOAssembler.INSTANCE;

  private final AccountService accountService;

  @Override
  public long createAccount(AccountModifyCommand createCommand) {
    // 转换对象
    var account =
        Account.builder().userId(LongId.valueOf(createCommand.getUserId(), "userId"))
                .build();
    // 行为发生
    account.save(accountService);
    // todo... 触发事件

    // 返回id
    return account.getId().getValue();
  }

  @Override
  public void updateAccount(AccountModifyCommand updateCommand) {
    // 转换对象
    var account =
        Account.builder()
            .id(AccountId.valueOf(updateCommand.getId(), "id"))
            .userId(LongId.valueOf(updateCommand.getUserId(), "userId"))
            .build();
    // 行为发生
    account.save(accountService);
    // todo... 触发事件

    return;
  }

  @Override
  public void deleteAccount(Long userId, Long accountId) {
    // 转换对象
    var account =
        Account.builder()
            .id(AccountId.valueOf(accountId, "id"))
            .userId(LongId.valueOf(userId, "userId"))
            .build();
    // 行为发生

    account.remove(accountService);
    // todo... 触发事件

    return;
  }

  @Override
  public AccountDTO queryAccount(Long userId, Long accountId) {
    // todo...
    return accountService
        .find(AccountId.valueOf(accountId, "id"))
        .map(accountDTOAssembler::toDTO)
        .get();
  }

  @Override
  public List<AccountDTO> queryAccounts(Long userId) {
    // todo... 实现

    return null;
  }
}
