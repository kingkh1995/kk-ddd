package com.kkk.op.user.application.service;

import com.kkk.op.support.model.command.AccountModifyCommand;
import com.kkk.op.support.model.dto.AccountDTO;
import java.util.List;

/**
 * todo... 查询方法拆分到 queryService<br>
 * ApplicationService供接口层调用，接口层理论对Domain层应该是无感知的 <br>
 * 入参为Query（查询操作），Command（写操作，返回执行结果），Event（已发生事件响应，通常是写，无返回结果）出参为DTO
 *
 * @author KaiKoo
 */
public interface AccountAppService {

  long createAccount(AccountModifyCommand createCommand);

  void updateAccount(AccountModifyCommand updateCommand);

  void deleteAccount(Long userId, Long accountId);

  AccountDTO queryAccount(Long userId, Long accountId);

  List<AccountDTO> queryAccounts(Long userId);
}
