package com.kkk.op.user.domain.service.impl;

import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.service.AccountService;
import com.kkk.op.user.domain.strategy.AccountModifyStrategyManager;
import com.kkk.op.user.repository.AccountRepository;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 1.查询类方法属于查询类实体类的行为，在对应的实体类中实现
 * 2.实体的行为仅涉及自身的状态变更，业务逻辑在实体类中
 * 3.多个实体的状态变更的行为业务逻辑在 domain service 中
 * 4.该类只做组件调用，参数的合法性均放置到对应的实体类总
 *
 * @author KaiKoo
 */
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final AccountModifyStrategyManager accountModifyStrategyManager;

    public AccountServiceImpl(@Autowired AccountRepository accountRepository,
            @Autowired AccountModifyStrategyManager accountModifyStrategyManager) {
        this.accountRepository = accountRepository;
        this.accountModifyStrategyManager = accountModifyStrategyManager;
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
    public LongId save(@NotNull Account entity) {
        return accountRepository.save(entity);
    }

    @Override
    public boolean allowModify(@NotNull Account oldAccount, @NotNull Account newAccount) {
        return accountModifyStrategyManager.allowModify(oldAccount, newAccount);
    }
}
