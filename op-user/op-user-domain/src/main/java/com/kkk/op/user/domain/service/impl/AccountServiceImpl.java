package com.kkk.op.user.domain.service.impl;

import com.kkk.op.support.type.LongId;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.service.AccountService;
import com.kkk.op.user.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author KaiKoo
 */
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(@Autowired AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account find(LongId id) {
        if (id == null) {
            return null;
        }
        return accountRepository.find(id);
    }

    @Override
    public void remove(Account entity) {
        Account account = this.find(entity.getId());
        // 逻辑校验
        if (account == null) {
            throw new IllegalArgumentException("id不存在");
        }
        // 移除
        accountRepository.remove(entity);
    }

    @Override
    public void save(Account entity) {
        Account account = this.find(entity.getId());
        if (account == null && entity.getId() != null) {
            throw new IllegalArgumentException("id不存在");
        }
        accountRepository.save(entity);
    }
}
