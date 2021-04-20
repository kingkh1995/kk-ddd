package com.kkk.op.user.application.service;

import com.kkk.op.support.models.user.AccountDTO;
import com.kkk.op.support.models.user.AccountQueryDTO;
import java.util.List;

/**
 * todo...
 * Application Service 供接口层调用，入参为Query（查询操作），Command（写操作，返回执行结果），Event（已发生事件响应，通常是写，无返回结果）等Entity，出参为DTO
 * @author KaiKoo
 */
public interface AccountApplicationService {

    AccountDTO find(Long id);

    void remove(Long id);

    Long save(AccountDTO dto);

    List<AccountDTO> list(AccountQueryDTO queryDTO);

}
