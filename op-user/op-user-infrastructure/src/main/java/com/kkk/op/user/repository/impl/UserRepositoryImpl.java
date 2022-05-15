package com.kkk.op.user.repository.impl;

import com.kkk.op.support.base.AggregateRepositorySupport;
import com.kkk.op.support.base.Kson;
import com.kkk.op.support.bean.ThreadLocalAggregateTrackingManager;
import com.kkk.op.support.exception.BusinessException;
import com.kkk.op.support.tracking.diff.Diff;
import com.kkk.op.user.converter.AccountDataConverter;
import com.kkk.op.user.converter.UserDataConverter;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.domain.type.AccountId;
import com.kkk.op.user.domain.type.UserId;
import com.kkk.op.user.persistence.AccountDO;
import com.kkk.op.user.persistence.AccountMapper;
import com.kkk.op.user.persistence.UserDO;
import com.kkk.op.user.persistence.UserMapper;
import com.kkk.op.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Aggregate类Repository实现类
 *
 * @author KaiKoo
 */
@Component
public class UserRepositoryImpl extends AggregateRepositorySupport<User, UserId>
    implements UserRepository {

  public static final String CACHE_NAME = "user:User";

  private final UserMapper userMapper;

  private final AccountMapper accountMapper;

  private final UserDataConverter userDataConverter;

  private final AccountDataConverter accountDataConverter;

  private final CacheManager cacheManager;

  public UserRepositoryImpl(
      final UserMapper userMapper,
      final AccountMapper accountMapper,
      final UserDataConverter userDataConverter,
      final AccountDataConverter accountDataConverter,
      final CacheManager cacheManager) {
    // 使用ThreadLocalAggregateTrackingManager
    super(
        ThreadLocalAggregateTrackingManager.<User, UserId>builder()
            .snapshooter(user -> Kson.convertValue(user, User.class))
            .build());
    this.userMapper = userMapper;
    this.accountMapper = accountMapper;
    this.userDataConverter = userDataConverter;
    this.accountDataConverter = accountDataConverter;
    this.cacheManager = cacheManager;
  }

  // ===============================================================================================

  /** 插入操作后一定要填补Id，让aggregateTrackingManager能取到Id值 */
  @Override
  protected void onInsert(@NotNull User aggregate) {
    // 插入User
    var userDO = userDataConverter.toData(aggregate);
    userMapper.insert(userDO);
    // 填补id
    aggregate.fillInId(UserId.of(userDO.getId()));
    // 循环插入Accounts
    var accounts = aggregate.getAccounts();
    if (!CollectionUtils.isEmpty(accounts)) {
      accounts.forEach(
          account -> {
            var accountDO = accountDataConverter.toData(account);
            accountMapper.insert(accountDO);
            // 填补id
            account.fillInId(AccountId.of(accountDO.getId()));
          });
    }
  }

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
                    newValue.fillInId(AccountId.of(accountDO.getId()));
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
  protected List<User> onSelectByIds(@NotEmpty Set<UserId> userIds) {
    // 查询缓存
    var cache = Objects.requireNonNull(this.cacheManager.getCache(CACHE_NAME));
    var list = new ArrayList<User>(userIds.size());
    var ids2Lookup =
        userIds.stream()
            .filter(
                id -> {
                  var optional = Optional.ofNullable(cache.get(id));
                  if (optional.isPresent()) {
                    optional.map(ValueWrapper::get).ifPresent(o -> list.add((User) o));
                    return false;
                  }
                  return true;
                })
            .collect(Collectors.toSet());
    if (ids2Lookup.isEmpty()) {
      return list;
    }
    // 查询并加载缓存
    var longs = ids2Lookup.stream().mapToLong(UserId::getValue).boxed().collect(Collectors.toSet());
    var accountDOListMap =
        accountMapper.selectByUserIds(longs).stream()
            .collect(Collectors.groupingBy(AccountDO::getUserId));
    var userMap =
        userMapper.selectByIds(longs).stream()
            .map(userDO -> userDataConverter.fromData(userDO, accountDOListMap.get(userDO.getId())))
            .collect(Collectors.toMap(User::getId, Function.identity()));
    ids2Lookup.forEach(
        userId -> {
          var user = userMap.get(userId);
          cache.put(userId, user);
          if (user != null) {
            list.add(user);
          }
        });
    return list;
  }

  // ===============================================================================================

  /** 以下重写父类方法，添加缓存和事务注解。 */
  @CacheEvict(cacheNames = CACHE_NAME, key = "#p0.id", beforeInvocation = true)
  @Transactional
  @Override
  public void save(User user) {
    super.save(user);
  }

  @CacheEvict(cacheNames = CACHE_NAME, key = "#p0.id", beforeInvocation = true)
  @Transactional
  @Override
  public void remove(User user) {
    super.remove(user);
  }

  @Cacheable(cacheNames = CACHE_NAME, sync = true)
  @Override
  public Optional<User> find(UserId userId) {
    return super.find(userId);
  }

  // ===============================================================================================

  /** 以下为自定义的方法 */
  private User findThenBuild(@NotNull UserDO userDO) {
    var user = userDataConverter.fromData(userDO, accountMapper.selectByUserId(userDO.getId()));
    // 添加追踪
    return this.getAggregateTrackingManager().attach(user);
  }

  @Override
  public Optional<User> find(String username) {
    return userMapper.selectByUsername(username).map(this::findThenBuild);
  }
}
