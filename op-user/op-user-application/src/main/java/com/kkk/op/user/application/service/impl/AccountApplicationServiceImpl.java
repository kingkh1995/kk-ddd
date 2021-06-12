package com.kkk.op.user.application.service.impl;

import com.kkk.op.support.models.command.AccountModifyCommand;
import com.kkk.op.support.models.dto.AccountDTO;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.application.service.AccountApplicationService;
import com.kkk.op.user.assembler.AccountDTOAssembler;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.service.AccountService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Service
public class AccountApplicationServiceImpl implements AccountApplicationService {

  private final AccountDTOAssembler accountDTOAssembler = AccountDTOAssembler.INSTANCE;

  private final AccountService accountService;

  public AccountApplicationServiceImpl(@Autowired AccountService accountService) {
    this.accountService = accountService;
  }

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
  public void updateAccount(LongId userId, LongId accountId, AccountModifyCommand updateCommand) {
    // 转换对象
    var account = Account.builder().id(accountId).userId(userId).build();
    // 行为发生
    account.save(accountService);
    // todo... 触发事件

  }

  @Override
  public void deleteAccount(LongId id) {
    var account = Account.builder().id(id).build();
    account.remove(accountService);
  }

  @Override
  public AccountDTO queryAccount(LongId id) {
    return accountDTOAssembler.toDTO(accountService.find(id));
  }

  @Override
  public List<AccountDTO> queryAccountsByUserId(Long userId) {
    // todo... 实现
    return null;
  }
}
