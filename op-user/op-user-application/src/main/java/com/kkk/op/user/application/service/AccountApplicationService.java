package com.kkk.op.user.application.service;

import com.kkk.op.support.model.command.AccountModifyCommand;
import com.kkk.op.support.model.dto.AccountDTO;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.types.AccountId;
import java.util.List;

/**
 * todo... <br>
 * ApplicationService 供接口层调用， <br>
 * 入参为Query（查询操作），Command（写操作，返回执行结果），Event（已发生事件响应，通常是写，无返回结果）等Entity， <br>
 * 出参为DTO
 *
 * @author KaiKoo
 */
public interface AccountApplicationService {

  long createAccount(LongId userId, AccountModifyCommand createCommand);

  void updateAccount(LongId userId, AccountId accountId, AccountModifyCommand updateCommand);

  void deleteAccount(AccountId accountId);

  AccountDTO queryAccount(AccountId accountId);

  List<AccountDTO> queryAccounts(LongId userId);
}
