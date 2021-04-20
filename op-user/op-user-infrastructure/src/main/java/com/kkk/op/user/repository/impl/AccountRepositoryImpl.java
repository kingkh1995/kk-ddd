package com.kkk.op.user.repository.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kkk.op.support.annotations.Cacheable;
import com.kkk.op.support.base.EntityRepositorySupport;
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
@Cacheable(autoCaching = false) // fixme... 暂时未开放redis功能
public class AccountRepositoryImpl extends EntityRepositorySupport<Account, LongId> implements
        AccountRepository {

    private final AccountDataConverter accountDataConverter = AccountDataConverter.INSTANCE;

    private final AccountMapper accountMapper;

    public AccountRepositoryImpl(
//            @Autowired DistributedLock distributedLock,
//            @Autowired CacheManager<Account> cacheManager,
            @Autowired AccountMapper accountMapper) {
//        super(distributedLock, cacheManager);
        super(null, null);
        this.accountMapper = accountMapper;
    }

    @Override
    protected String generateCacheKey(LongId longId) {
        return String.format("op-user:account:%s", longId.stringValue());
    }

    @Override
    protected Account onSelect(@NotNull LongId longId) {
        return accountDataConverter.fromData(accountMapper.selectById(longId.getValue()));
    }

    @Override
    protected void onDelete(@NotNull Account entity) {
        accountMapper.delete(Wrappers.query(accountDataConverter.toData(entity)));
    }

    @Override
    protected void onInsert(@NotNull Account entity) {
        var data = accountDataConverter.toData(entity);
        accountMapper.insert(data);
        // 填补id
        entity.fillInId(LongId.valueOf(data.getId()));
    }

    @Override
    protected void onUpdate(@NotNull Account entity) {
        accountMapper.updateById(accountDataConverter.toData(entity));
    }

    @Override
    protected List<Account> onSelectByIds(@NotEmpty Set<LongId> longIds) {
        return accountDataConverter.fromData(accountMapper.selectBatchIds(longIds));
    }

}
