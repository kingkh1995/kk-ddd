package com.kkk.op.user.application.service;

import com.kkk.op.support.models.dto.AccountDTO;

/**
 * Application Service 供controller调用，参数使用dto和基本数据类型
 * @author KaiKoo
 */
public interface AccountAppService {

    AccountDTO find(Long id);

    void remove(Long id);

    Long save(AccountDTO dto);

}
