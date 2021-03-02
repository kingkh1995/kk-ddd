package com.kkk.op.user.repository.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.converter.AccountDataConverter;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.persistence.AccountDO;
import com.kkk.op.user.persistence.mapper.AccountMapper;
import com.kkk.op.user.repository.AccountRepository;
import javax.validation.constraints.NotNull;

/**
 *
 * @author KaiKoo
 */
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountMapper accountMapper;
    private final AccountDataConverter accountDataConverter;

    public AccountRepositoryImpl(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
        accountDataConverter = AccountDataConverter.getInstance();
    }

    @Override
    public Account find(@NotNull LongId longId) {
        return accountDataConverter.fromData(accountMapper.selectById(longId.getValue()));
    }

    @Override
    public void remove(@NotNull Account entity) {
        accountMapper.delete(Wrappers.query(accountDataConverter.toData(entity)));
    }

    @Override
    public LongId save(@NotNull Account entity) {
        AccountDO data = accountDataConverter.toData(entity);
        if (data.getId() != null) {
            accountMapper.updateById(data);
        } else {
            accountMapper.insert(data);
        }
        return new LongId(data.getId());
    }
}
