package com.kkk.op.user.repository.impl;

import com.kkk.op.support.annotation.AutoCaching;
import com.kkk.op.support.base.AggregateRepositorySupport;
import com.kkk.op.support.bean.Kson;
import com.kkk.op.support.bean.NettyDelayer;
import com.kkk.op.support.bean.ThreadLocalAggregateTrackingManager;
import com.kkk.op.support.changeTracking.diff.Diff;
import com.kkk.op.support.exception.BusinessException;
import com.kkk.op.support.marker.Cache;
import com.kkk.op.user.converter.AccountDataConverter;
import com.kkk.op.user.converter.UserDataConverter;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.domain.types.AccountId;
import com.kkk.op.user.domain.types.UserId;
import com.kkk.op.user.persistence.mapper.AccountMapper;
import com.kkk.op.user.persistence.mapper.UserMapper;
import com.kkk.op.user.persistence.po.AccountDO;
import com.kkk.op.user.persistence.po.UserDO;
import com.kkk.op.user.repository.UserRepository;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
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
public class UserRepositoryImpl extends AggregateRepositorySupport<User, UserId>
    implements UserRepository {

  private final UserMapper userMapper;

  private final AccountMapper accountMapper;

  private final UserDataConverter userDataConverter;

  private final AccountDataConverter accountDataConverter;

  private final Kson kson;

  private final NettyDelayer delayer;

  public UserRepositoryImpl(
      final Cache cache,
      final UserMapper userMapper,
      final AccountMapper accountMapper,
      final UserDataConverter userDataConverter,
      final AccountDataConverter accountDataConverter,
      final Kson kson,
      final NettyDelayer delayer) {
    super(User.class);
    this.userMapper = userMapper;
    this.accountMapper = accountMapper;
    this.userDataConverter = userDataConverter;
    this.accountDataConverter = accountDataConverter;
    this.kson = kson;
    this.delayer = delayer;
    super.setCache(cache);
    // 使用ThreadLocalAggregateTrackingManager
    super.setAggregateTrackingManager(
        ThreadLocalAggregateTrackingManager.<User, UserId>builder()
            .snapshooter(user -> this.kson.convertValue(user, User.class))
            .build());
  }

  @Override
  public void cacheDelayRemoveAsync(UserId userId) {
    // 使用时间轮算法，延迟两秒删除缓存
    delayer.delay(() -> cacheRemove(userId), 2, TimeUnit.SECONDS);
  }

  /** 插入操作后一定要填补Id，让aggregateTrackingManager能取到Id值 */
  @Transactional
  @Override
  protected void onInsert(@NotNull User aggregate) {
    // 插入User
    var userDO = userDataConverter.toData(aggregate);
    userMapper.insert(userDO);
    // 填补id
    aggregate.fillInId(UserId.from(userDO.getId()));
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
                  // 新增情况
                  case Added -> {
                    var accountDO = accountDataConverter.toData(newValue);
                    accountMapper.insert(accountDO);
                    // 填补id
                    newValue.fillInId(AccountId.from(accountDO.getId()));
                  }
                  // 移除情况
                  case Removed -> accountMapper.deleteById(oldValue.getId().getValue());
                  // 更新情况
                  case Modified -> {
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
  protected Optional<User> onSelect(@NotNull UserId userId) {
    return userMapper
        .selectById(userId.getValue())
        .map(
            userDO ->
                userDataConverter.fromData(userDO, accountMapper.selectByUserId(userDO.getId())));
  }

  @Override
  protected Map<UserId, User> onSelectByIds(@NotEmpty Set<UserId> userIds) {
    var longs = userIds.stream().mapToLong(UserId::getValue).boxed().collect(Collectors.toSet());
    var accountDOListMap =
        accountMapper.selectByUserIds(longs).stream()
            .collect(Collectors.groupingBy(AccountDO::getUserId));
    return userMapper.selectByIds(longs).stream()
        .map(userDO -> userDataConverter.fromData(userDO, accountDOListMap.get(userDO.getId())))
        .collect(Collectors.toMap(User::getId, Function.identity()));
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return userMapper.selectByUsername(username).map(this::findThenBuild);
  }

  private User findThenBuild(@NotNull UserDO userDO) {
    var user = userDataConverter.fromData(userDO, accountMapper.selectByUserId(userDO.getId()));
    // 添加缓存
    cachePutIfAbsent(user.getId(), user);
    // 添加追踪
    attach(user);
    return user;
  }
}
