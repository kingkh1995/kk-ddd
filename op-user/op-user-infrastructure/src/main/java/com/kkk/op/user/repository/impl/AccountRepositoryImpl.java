package com.kkk.op.user.repository.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.converter.AccountDataConverter;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.persistence.mapper.AccountMapper;
import com.kkk.op.user.repository.AccountRepository;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Entity类Repository实现类
 * @author KaiKoo
 */
@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountDataConverter accountDataConverter = AccountDataConverter.INSTANCE;

    private final AccountMapper accountMapper;

    public AccountRepositoryImpl(@Autowired AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Override
    public Account find(@NotNull LongId id) {
        return accountDataConverter.fromData(accountMapper.selectById(id.getValue()));
    }

    @Override
    public void remove(@NotNull Account entity) {
        accountMapper.delete(Wrappers.query(accountDataConverter.toData(entity)));
    }

    @Override
    public void save(@NotNull Account entity) {
        var data = accountDataConverter.toData(entity);
        if (data.getId() == null) {
            accountMapper.insert(data);
            // 填补id
            entity.fillInId(new LongId(data.getId()));
            return;
        }
        accountMapper.updateById(data);
    }

    @Override
    public List<Account> list(@NotEmpty Set<LongId> ids) {
        return accountDataConverter.fromData(accountMapper.selectBatchIds(ids));
    }

}
