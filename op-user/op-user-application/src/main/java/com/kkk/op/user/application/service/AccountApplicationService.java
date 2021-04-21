package com.kkk.op.user.application.service;

import com.kkk.op.support.models.command.AccountCreateCommand;
import com.kkk.op.support.models.command.AccountUpdateCommand;
import com.kkk.op.support.models.dto.AccountDTO;

/**
 * todo...
 * Application Service 供接口层调用，入参为Query（查询操作），Command（写操作，返回执行结果），Event（已发生事件响应，通常是写，无返回结果）等Entity，出参为DTO
 * @author KaiKoo
 */
public interface AccountApplicationService {

    long createAccount(AccountCreateCommand createCommand);

    void updateAccount(AccountUpdateCommand updateCommand);

    void deleteAccount(Long id);

    AccountDTO queryAccountById(Long id);

}
