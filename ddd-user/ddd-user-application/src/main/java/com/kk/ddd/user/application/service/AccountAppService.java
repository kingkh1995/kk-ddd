package com.kk.ddd.user.application.service;

import com.kk.ddd.support.model.command.AccountModifyCommand;
import com.kk.ddd.support.model.dto.AccountDTO;
import java.util.List;

/**
 * ApplicationService供接口层调用，接口层理论对Domain层应该是无感知的 <br>
 * 入参为Query（查询操作），Command（写操作，返回执行结果），Event（已发生事件响应，通常是写，无返回结果）出参为DTO <br>
 * todo... 为ApplicationService添加全局事务管理切面 <br>
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
