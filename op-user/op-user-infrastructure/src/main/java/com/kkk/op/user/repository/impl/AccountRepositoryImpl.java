package com.kkk.op.user.repository.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kkk.op.support.annotations.Cacheable;
import com.kkk.op.support.bean.EntityRepositorySupport;
import com.kkk.op.support.marker.CacheManager;
import com.kkk.op.support.marker.DistributedReentrantLock;
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
@Cacheable // 开启自动缓存
public class AccountRepositoryImpl extends EntityRepositorySupport<Account, LongId> implements
        AccountRepository {

    private final AccountDataConverter accountDataConverter = AccountDataConverter.INSTANCE;

    private final AccountMapper accountMapper;

    public AccountRepositoryImpl(
            @Autowired DistributedReentrantLock distributedReentrantLock,
            @Autowired CacheManager<Account> cacheManager,
            @Autowired AccountMapper accountMapper) {
        super(distributedReentrantLock, cacheManager);
        this.accountMapper = accountMapper;
    }

    @Override
    protected Account onSelect(@NotNull LongId longId) {
        return accountDataConverter.fromData(accountMapper.selectById(longId.getId()));
    }

    @Override
    protected void onDelete(@NotNull Account entity) {
        accountMapper.delete(Wrappers.query(accountDataConverter.toData(entity)));
    }

    @Override
    protected void onInsertOrUpdate(@NotNull Account entity) {
        var data = accountDataConverter.toData(entity);
        if (data.getId() != null) {
            accountMapper.updateById(data);
            return;
        }
        accountMapper.insert(data);
        // 填补id
        entity.fillInId(new LongId(data.getId()));
    }

    @Override
    protected List<Account> onSelectByIds(@NotEmpty Set<LongId> longIds) {
        return accountDataConverter.fromData(accountMapper.selectBatchIds(longIds));
    }

}
