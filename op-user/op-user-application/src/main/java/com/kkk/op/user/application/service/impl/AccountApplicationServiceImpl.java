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
  public long createAccount(LongId userId, AccountModifyCommand createCommand) {
    // 转换对象
    var account = Account.builder().userId(userId).build();
    // 行为发生
    account.save(accountService);
    // todo... 触发事件

    // 返回id
    return account.getId().getValue();
  }

  @Override
  public void updateAccount(
      LongId userId, AccountId accountId, AccountModifyCommand updateCommand) {
    // 转换对象
    var account = Account.builder().id(accountId).userId(userId).build();
    // 行为发生
    account.save(accountService);
    // todo... 触发事件

  }

  @Override
  public void deleteAccount(AccountId accountId) {
    var account = Account.builder().id(accountId).build();
    account.remove(accountService);
  }

  @Override
  public AccountDTO queryAccount(AccountId accountId) {
    return accountDTOAssembler.toDTO(accountService.find(accountId));
  }

  @Override
  public List<AccountDTO> queryAccounts(LongId userId) {
    // todo... 实现
    return null;
  }
}
