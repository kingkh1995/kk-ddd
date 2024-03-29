package com.kk.ddd.user.repository.impl;

import com.kk.ddd.support.bean.Jackson;
import com.kk.ddd.support.diff.Diff;
import com.kk.ddd.support.exception.BusinessException;
import com.kk.ddd.support.repository.AggregateRepositorySupport;
import com.kk.ddd.support.repository.ThreadLocalAggregateTrackingManager;
import com.kk.ddd.user.converter.AccountDataConverter;
import com.kk.ddd.user.converter.UserDataConverter;
import com.kk.ddd.user.domain.entity.Account;
import com.kk.ddd.user.domain.entity.User;
import com.kk.ddd.user.domain.type.AccountId;
import com.kk.ddd.user.domain.type.UserId;
import com.kk.ddd.user.domain.type.Username;
import com.kk.ddd.user.persistence.AccountMapper;
import com.kk.ddd.user.persistence.AccountPO;
import com.kk.ddd.user.persistence.UserMapper;
import com.kk.ddd.user.persistence.UserPO;
import com.kk.ddd.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aggregate类Repository实现类
 *
 * @author KaiKoo
 */
@Repository
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
            .snapshooter(user -> Jackson.convert(user, User.class))
            .build());
    this.userMapper = userMapper;
    this.accountMapper = accountMapper;
    this.userDataConverter = userDataConverter;
    this.accountDataConverter = accountDataConverter;
    this.cacheManager = cacheManager;
  }

  private Cache getCache() {
    return Objects.requireNonNull(this.cacheManager.getCache(CACHE_NAME));
  }

  // ===============================================================================================

  /** 重写父类方法，添加缓存和事务注解。 */
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

  // ===============================================================================================

  @Override
  protected void onInsert(@NotNull User aggregate) {
    // 插入User
    var userPO = userDataConverter.toData(aggregate);
    userMapper.insert(userPO);
    // todo... 生成分布式ID
    aggregate.fillInId(UserId.of(userPO.getId()));
    // 循环插入Accounts
    Stream.ofNullable(aggregate.getAccounts())
        .flatMap(Collection::stream)
        .forEach(this::insertAccount);
  }

  private void insertAccount(Account account) {
    var accountPO = accountDataConverter.toData(account);
    // 插入
    accountMapper.insert(accountPO);
    // todo... 生成分布式ID
    account.fillInId(AccountId.of(accountPO.getId()));
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
                  var accountPO = accountDataConverter.toData(newValue);
                  accountMapper.insert(accountPO);
                  // 填补id
                  newValue.fillInId(AccountId.of(accountPO.getId()));
                }
                  // 移除情况
                case Removed -> accountMapper.deleteById(accountDataConverter.toData(oldValue));
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
    // 只删除User
    userMapper.logicalDeleteById(userDataConverter.toData(aggregate));
  }

  // ===============================================================================================

  /** 以下实现查询方法，添加缓存逻辑 */
  @Override
  protected Optional<User> onSelect(@NotNull UserId userId) {
    // 查询缓存，不存在则使用valueLoader加载缓存。
    return Optional.ofNullable(getCache().get(userId, () -> this.selectById(userId)));
  }

  private User selectById(@NotNull UserId userId) {
    return userMapper
        .selectById(userId.getValue())
        .map(
            userPO ->
                userDataConverter.fromData(userPO, accountMapper.selectByUserId(userPO.getId())))
        .orElse(null);
  }

  @Override
  protected List<User> onSelectByIds(@NotEmpty Set<UserId> userIds) {
    // 查询缓存
    var cache = getCache();
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
    var accountPOListMap =
        accountMapper.selectByUserIds(longs).stream()
            .collect(Collectors.groupingBy(AccountPO::getUserId));
    var userMap =
        userMapper.selectByIds(longs).stream()
            .map(userPO -> userDataConverter.fromData(userPO, accountPOListMap.get(userPO.getId())))
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

  /** 以下为自定义的方法 */
  private User findThenBuild(@NotNull UserPO userPO) {
    var user = userDataConverter.fromData(userPO, accountMapper.selectByUserId(userPO.getId()));
    // 添加追踪
    return this.getAggregateTrackingManager().attach(user);
  }

  @Override
  public Optional<User> find(Username name) {
    return userMapper.selectByName(name.getValue()).map(this::findThenBuild);
  }
}
