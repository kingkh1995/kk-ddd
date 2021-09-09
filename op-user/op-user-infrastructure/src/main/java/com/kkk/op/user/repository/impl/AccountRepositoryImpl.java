package com.kkk.op.user.repository.impl;

import com.kkk.op.support.base.EntityRepositorySupport;
import com.kkk.op.support.marker.CacheManager;
import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.user.converter.AccountDataConverter;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.types.AccountId;
import com.kkk.op.user.persistence.mapper.UserMapper;
import com.kkk.op.user.repository.AccountRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Entity类Repository实现类 （仅模板，实际上聚合根子实体不应该单独存在）
 *
 * @author KaiKoo
 */
@Repository
public class AccountRepositoryImpl extends EntityRepositorySupport<Account, AccountId>
    implements AccountRepository {

  private final AccountDataConverter accountDataConverter = AccountDataConverter.INSTANCE;

  private final UserMapper userMapper;

  public AccountRepositoryImpl(
      @Autowired DistributedLock distributedLock,
      @Autowired(required = false) CacheManager cacheManager,
      @Autowired UserMapper userMapper) {
    super(distributedLock, cacheManager);
    this.userMapper = userMapper;
  }

  @Override
  protected Account onSelect(@NotNull AccountId accountId) {
    return accountDataConverter.fromData(userMapper.selectAccountByPK(accountId.getValue()));
  }

  @Override
  protected void onDelete(@NotNull Account entity) {
    userMapper.deleteAccountByPK(entity.getId().getValue());
  }

  @Override
  protected void onInsert(@NotNull Account entity) {
    var data = accountDataConverter.toData(entity);
    userMapper.insertAccount(data);
    // 填补id
    entity.fillInId(AccountId.from(data.getId()));
  }

  @Override
  protected void onUpdate(@NotNull Account entity) {
    userMapper.updateAccountByPK(accountDataConverter.toData(entity));
  }

  @Override
  protected List<Account> onSelectByIds(@NotEmpty Set<AccountId> accountIds) {
    var ids =
        accountIds.stream().mapToLong(AccountId::longValue).boxed().collect(Collectors.toSet());
    // todo...
    return null;
  }
}
