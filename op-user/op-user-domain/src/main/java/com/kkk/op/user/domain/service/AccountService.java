package com.kkk.op.user.domain.service;

import com.kkk.op.support.type.LongId;
import com.kkk.op.user.domain.entity.Account;

/**
 *
 * @author KaiKoo
 */
public interface AccountService {

    /**
     * 通过 ID 寻找 Entity。
     */
    Account find(LongId id);

    /**
     * 将一个 Entity 从 Repository 移除
     */
    void remove(Account entity);

    /**
     * 保存一个 Entity
     */
    void save(Account entity);

}
