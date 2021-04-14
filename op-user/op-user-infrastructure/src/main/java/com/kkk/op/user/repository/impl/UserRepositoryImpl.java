package com.kkk.op.user.repository.impl;

import com.google.common.collect.ImmutableMap;
import com.kkk.op.support.annotations.Cacheable;
import com.kkk.op.support.bean.AggregateRepositorySupport;
import com.kkk.op.support.changeTracking.ThreadLocalAggregateTrackingManager;
import com.kkk.op.support.changeTracking.diff.CollectionDiff;
import com.kkk.op.support.changeTracking.diff.DiffType;
import com.kkk.op.support.changeTracking.diff.EntityDiff;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.converter.AccountDataConverter;
import com.kkk.op.user.converter.UserDataConverter;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.persistence.mapper.AccountMapper;
import com.kkk.op.user.persistence.mapper.UserMapper;
import com.kkk.op.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Aggregate类Repository实现类
 * @author KaiKoo
 */
@Repository
@Cacheable(autoCaching = false) // fixme... 暂时未开放redis功能
public class UserRepositoryImpl extends AggregateRepositorySupport<User, LongId> implements
        UserRepository {

    private final UserDataConverter userDataConverter = UserDataConverter.INSTANCE;

    private final AccountDataConverter accountDataConverter = AccountDataConverter.INSTANCE;

    private final UserMapper userMapper;

    private final AccountMapper accountMapper;

    public UserRepositoryImpl(
//            @Autowired DistributedLock distributedLock,
//            @Autowired CacheManager<User> cacheManager,
            @Autowired UserMapper userMapper,
            @Autowired AccountMapper accountMapper) {
        // 使用ThreadLocalAggregateTrackingManager
//        super(distributedLock, cacheManager, new ThreadLocalAggregateTrackingManager());
        super(null, null, new ThreadLocalAggregateTrackingManager());
        this.userMapper = userMapper;
        this.accountMapper = accountMapper;
    }

    /**
     * 插入操作后一定要填补Id，让aggregateTrackingManager能取到Id值
     */
    @Transactional
    @Override
    protected void onInsert(@NotNull User aggregate) {
        // 插入User
        var userDO = userDataConverter.toData(aggregate);
        userMapper.insert(userDO);
        // 填补id
        aggregate.fillInId(new LongId(userDO.getId()));
        // 循环插入Account
        var accounts = aggregate.getAccounts();
        if (!CollectionUtils.isEmpty(accounts)) {
            accounts.forEach(account -> {
                var accountDO = accountDataConverter.toData(account);
                accountMapper.insert(accountDO);
                // 填补id
                account.fillInId(new LongId(accountDO.getId()));
            });
        }
    }

    @Override
    protected List<User> onSelectByIds(@NotEmpty Set<LongId> longIds) {
        return null;
    }

    @Override
    protected User onSelect(@NotNull LongId longId) {
        // 查询User
        var user = userDataConverter.fromData(userMapper.selectById(longId.getId()));
        // 查询Account
        user.setAccounts(new ArrayList<>());
        accountMapper.selectByMap(ImmutableMap.of("user_id", longId.getId())).forEach(
                accountDO -> user.getAccounts().add(accountDataConverter.fromData(accountDO)));
        return user;
    }

    @Transactional
    @Override
    protected void onUpdate(@NotNull User aggregate, @NotNull EntityDiff diff) {
        // 更新User
        if (diff.isSelfModified()) {
            userMapper.updateById(userDataConverter.toData(aggregate));
        }
        // 处理Account
        var collectionDiff = (CollectionDiff) diff.get("accounts");
        if (collectionDiff != null) {
            var iterator = collectionDiff.iterator();
            while (iterator.hasNext()) {
                var entityDiff = (EntityDiff) iterator.next();
                // 移除情况
                if (entityDiff.getType() == DiffType.Removed) {
                    var oldValue = (Account) entityDiff.getOldValue();
                    accountMapper.deleteById(oldValue.getId().getId());
                }
                // 新增情况
                if (entityDiff.getType() == DiffType.Added) {
                    var newValue = (Account) entityDiff.getNewValue();
                    var accountDO = accountDataConverter.toData(newValue);
                    accountMapper.insert(accountDO);
                    // 填补id
                    newValue.fillInId(new LongId(accountDO.getId()));
                }
                // 更新情况
                if (entityDiff.getType() == DiffType.Modified) {
                    accountMapper.updateById(
                            accountDataConverter.toData((Account) entityDiff.getNewValue()));
                }
            }
        }
    }

    @Transactional
    @Override
    protected void onDelete(@NotNull User aggregate) {
        // 删除User
        userMapper.deleteById(aggregate.getId().getId());
        // 删除Account
        var accountIdList = aggregate.getAccounts().stream().map(Account::getId)
                .map(LongId::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(accountIdList)) {
            accountMapper.deleteBatchIds(accountIdList);
        }
    }

}
