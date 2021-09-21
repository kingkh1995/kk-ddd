package com.kkk.op.user.repository.impl;

import com.kkk.op.support.base.AggregateRepositorySupport;
import com.kkk.op.support.base.AutoCaching;
import com.kkk.op.support.bean.ThreadLocalAggregateTrackingManager;
import com.kkk.op.support.changeTracking.Snapshooter;
import com.kkk.op.support.changeTracking.diff.Diff;
import com.kkk.op.support.exception.BusinessException;
import com.kkk.op.support.marker.CacheManager;
import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.converter.AccountDataConverter;
import com.kkk.op.user.converter.UserDataConverter;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.domain.types.AccountId;
import com.kkk.op.user.persistence.mapper.AccountMapper;
import com.kkk.op.user.persistence.mapper.UserMapper;
import com.kkk.op.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Aggregate类Repository实现类
 *
 * @author KaiKoo
 */
@AutoCaching // 开启自动缓存功能
@Repository
public class UserRepositoryImpl extends AggregateRepositorySupport<User, LongId>
    implements UserRepository {

  private final UserDataConverter userDataConverter = UserDataConverter.INSTANCE;

  private final AccountDataConverter accountDataConverter = AccountDataConverter.INSTANCE;

  private final UserMapper userMapper;

  private final AccountMapper accountMapper;

  public UserRepositoryImpl(
      final DistributedLock distributedLock,
      final CacheManager cacheManager,
      final UserMapper userMapper,
      final AccountMapper accountMapper) {
    // 使用ThreadLocalAggregateTrackingManager
    super(
        distributedLock,
        cacheManager,
        ThreadLocalAggregateTrackingManager.<User, LongId>builder()
            .snapshooter(Snapshooter.identity()) // todo... snapshooter
            .build());
    this.userMapper = userMapper;
    this.accountMapper = accountMapper;
  }

  /** 插入操作后一定要填补Id，让aggregateTrackingManager能取到Id值 */
  @Transactional
  @Override
  protected void onInsert(@NotNull User aggregate) {
    // 插入User
    var userDO = userDataConverter.toData(aggregate);
    userMapper.insert(userDO);
    // 填补id
    aggregate.fillInId(LongId.from(userDO.getId()));
    // 循环插入Accounts
    var accounts = aggregate.getAccounts();
    if (!CollectionUtils.isEmpty(accounts)) {
      accounts.forEach(
          account -> {
            var accountDO = accountDataConverter.toData(account);
            accountMapper.insert(accountDO);
            // 填补id
            account.fillInId(AccountId.from(accountDO.getId()));
          });
    }
  }

  @Transactional
  @Override
  protected void onUpdate(@NotNull User aggregate, @NotNull Diff diff) {
    // 更新User
    if (diff.isSelfModified()) {
      userMapper.updateById(userDataConverter.toData(aggregate));
    }
    // 处理Account
    diff.lambdaGet(User::getAccounts)
        .elements()
        .forEachRemaining(
            accountDiff -> {
              var oldValue = (Account) accountDiff.getOldValue();
              var newValue = (Account) accountDiff.getNewValue();
              switch (accountDiff.getChangeType()) {
                case Added -> {
                  // 新增情况
                  var accountDO = accountDataConverter.toData(newValue);
                  accountMapper.insert(accountDO);
                  // 填补id
                  newValue.fillInId(AccountId.from(accountDO.getId()));
                }
                case Removed -> {
                  // 移除情况
                  accountMapper.deleteById(oldValue.getId().getValue());
                }
                case Modified -> {
                  // 更新情况
                  if (accountMapper.updateById(accountDataConverter.toData(newValue)) < 1) {
                    throw new BusinessException("Update failed by OCC!");
                  }
                }
              }
            });
  }

  @Transactional
  @Override
  protected void onDelete(@NotNull User aggregate) {
    // 删除User
    userMapper.deleteById(aggregate.getId().getValue());
    // 删除Accounts
    accountMapper.deleteByUserId(aggregate.getId().getValue());
  }

  @Override
  protected Optional<User> onSelect(@NotNull LongId longId) {
    // 查询User
    var l = longId.getValue();
    var op = userMapper.selectById(l).map(userDataConverter::fromData);
    // 查询Accounts
    op.ifPresent(
        user ->
            user.setAccounts(accountDataConverter.fromData(accountMapper.selectListByUserId(l))));
    return op;
  }

  @Override
  protected List<User> onSelectByIds(@NotEmpty Set<LongId> longIds) {
    // todo...
    return null;
  }
}
