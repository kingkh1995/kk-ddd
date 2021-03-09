package com.kkk.op.user.application.service;

import com.kkk.op.support.models.dto.AccountDTO;

/**
 *
 * @author KaiKoo
 */
public interface AccountAppService {

    /**
     * 通过 ID 寻找 Entity。
     */
    AccountDTO find(Long id);

    /**
     * 将一个 Entity 从 Repository 移除
     */
    void remove(AccountDTO dto);

    /**
     * 保存一个 Entity
     */
    void save(AccountDTO dto);

}
