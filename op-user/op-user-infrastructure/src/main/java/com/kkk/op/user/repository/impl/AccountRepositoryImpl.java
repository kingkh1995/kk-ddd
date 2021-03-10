package com.kkk.op.user.repository.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kkk.op.support.type.LongId;
import com.kkk.op.user.converter.AccountDataConverter;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.persistence.AccountDO;
import com.kkk.op.user.persistence.mapper.AccountMapper;
import com.kkk.op.user.repository.AccountRepository;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Entity类Repository实现类
 * @author KaiKoo
 */
@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountMapper accountMapper;
    private final AccountDataConverter accountDataConverter;

    public AccountRepositoryImpl(@Autowired AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
        this.accountDataConverter = AccountDataConverter.getInstance();
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
        if (data.getId() == null) {
            accountMapper.insert(data);
            return new LongId(data.getId());
        }
        accountMapper.updateById(data);
        return entity.getId();
    }
}
