package com.kkk.op.user.repository.impl;

import com.google.common.collect.ImmutableMap;
import com.kkk.op.support.changeTracking.AggregateRepositorySupport;
import com.kkk.op.support.changeTracking.ThreadLocalAggregateTrackingManager;
import com.kkk.op.support.changeTracking.diff.CollectionDiff;
import com.kkk.op.support.changeTracking.diff.Diff;
import com.kkk.op.support.changeTracking.diff.DiffType;
import com.kkk.op.support.changeTracking.diff.EntityDiff;
import com.kkk.op.support.type.LongId;
import com.kkk.op.user.converter.AccountDataConverter;
import com.kkk.op.user.converter.UserDataConverter;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.persistence.AccountDO;
import com.kkk.op.user.persistence.UserDO;
import com.kkk.op.user.persistence.mapper.AccountMapper;
import com.kkk.op.user.persistence.mapper.UserMapper;
import com.kkk.op.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
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
public class UserRepositoryImpl extends AggregateRepositorySupport<User, LongId> implements
        UserRepository {

    private final UserMapper userMapper;
    private final AccountMapper accountMapper;
    private final UserDataConverter userDataConverter;
    private final AccountDataConverter accountDataConverter;


    public UserRepositoryImpl(@Autowired UserMapper userMapper, @Autowired AccountMapper accountMapper) {
        // 使用ThreadLocalAggregateTrackingManager
        super(new ThreadLocalAggregateTrackingManager());
        this.userMapper = userMapper;
        this.accountMapper = accountMapper;
        this.userDataConverter = UserDataConverter.getInstance();
        this.accountDataConverter = AccountDataConverter.getInstance();
    }

    /**
     * 插入操作后一定要回填Id，让aggregateTrackingManager能取到Id值
     */
    @Transactional
    @Override
    protected void onInsert(@NotNull User aggregate) {
        // 插入User
        UserDO userDO = userDataConverter.toData(aggregate);
        userMapper.insert(userDO);
        // 回填id
        LongId userId = new LongId(userDO.getId());
        aggregate.setId(userId);
        // 循环插入Account
        List<Account> accounts = aggregate.getAccounts();
        if (!CollectionUtils.isEmpty(accounts)) {
            accounts.forEach(account -> {
                AccountDO accountDO = accountDataConverter.toData(account);
                accountMapper.insert(accountDO);
                // 回填id
                account.setId(new LongId(accountDO.getId()));
            });
        }
    }

    @Override
    protected User onSelect(@NotNull LongId longId) {
        // 查询User
        User user = userDataConverter.fromData(userMapper.selectById(longId.getValue()));
        // 查询Account
        user.setAccounts(new ArrayList<>());
        accountMapper.selectByMap(ImmutableMap.of("user_id", longId.getValue())).forEach(
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
        // 更新Account
        CollectionDiff collectionDiff = (CollectionDiff) diff.get("accounts");
        if (collectionDiff != null) {
            Iterator<Diff> iterator = collectionDiff.iterator();
            while (iterator.hasNext()) {
                EntityDiff entityDiff = (EntityDiff) iterator.next();
                if (entityDiff.getType() == DiffType.Removed) {
                    Account oldValue = (Account) entityDiff.getOldValue();
                    accountMapper.deleteById(oldValue.getId().getValue());
                }
                if (entityDiff.getType() == DiffType.Added) {
                    Account newValue = (Account) entityDiff.getNewValue();
                    AccountDO accountDO = accountDataConverter.toData(newValue);
                    accountMapper.insert(accountDO);
                    // 回填id
                    newValue.setId(new LongId(accountDO.getId()));
                }
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
        userMapper.deleteById(aggregate.getId().getValue());
        // 删除Account
        List<Long> accountIdList = aggregate.getAccounts().stream().map(Account::getId)
                .map(LongId::getValue).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(accountIdList)) {
            accountMapper.deleteBatchIds(accountIdList);
        }
    }
}
