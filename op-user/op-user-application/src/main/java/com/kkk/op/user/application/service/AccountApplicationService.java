package com.kkk.op.user.application.service;

import com.kkk.op.support.models.command.AccountModifyCommand;
import com.kkk.op.support.models.dto.AccountDTO;
import com.kkk.op.support.types.LongId;
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

  void updateAccount(LongId userId, LongId accountId, AccountModifyCommand updateCommand);

  void deleteAccount(LongId id);

  AccountDTO queryAccount(LongId id);

  List<AccountDTO> queryAccountsByUserId(Long userId);
}
