package com.kkk.op.user.domain.service.impl;

import com.kkk.op.support.type.LongId;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.service.AccountService;
import com.kkk.op.user.repository.AccountRepository;
import javax.validation.constraints.NotNull;

/**
 *
 * @author KaiKoo
 */
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account find(@NotNull LongId id) {
        return accountRepository.find(id);
    }

    @Override
    public void remove(@NotNull Account entity) {
        accountRepository.remove(entity);
    }

    @Override
    public void save(@NotNull Account entity) {
        accountRepository.save(entity);
    }
}
