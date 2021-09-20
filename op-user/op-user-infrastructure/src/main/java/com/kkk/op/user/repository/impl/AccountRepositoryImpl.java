package com.kkk.op.user.repository.impl;

import com.kkk.op.support.base.EntityRepositorySupport;
import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.user.converter.AccountDataConverter;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.types.AccountId;
import com.kkk.op.user.persistence.mapper.AccountMapper;
import com.kkk.op.user.repository.AccountRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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

  private final AccountMapper accountMapper;

  public AccountRepositoryImpl(
      final DistributedLock distributedLock, final AccountMapper accountMapper) {
    super(distributedLock, null); // 不开启AutoCaching则不需要CacheManager
    this.accountMapper = accountMapper;
  }

  @Override
  protected void onInsert(@NotNull Account entity) {
    var data = accountDataConverter.toData(entity);
    accountMapper.insert(data);
    // 填补id
    entity.fillInId(AccountId.from(data.getId()));
  }

  @Override
  protected void onUpdate(@NotNull Account entity) {
    // todo... 判断乐观锁是否更新成功
    accountMapper.updateById(accountDataConverter.toData(entity));
  }

  @Override
  protected void onDelete(@NotNull Account entity) {
    accountMapper.deleteById(entity.getId().getValue());
  }

  @Override
  protected Optional<Account> onSelect(@NotNull AccountId accountId) {
    return accountMapper.selectById(accountId.getValue()).map(accountDataConverter::fromData);
  }

  @Override
  protected List<Account> onSelectByIds(@NotEmpty Set<AccountId> accountIds) {
    var ids =
        accountIds.stream().mapToLong(AccountId::longValue).boxed().collect(Collectors.toSet());
    // todo...
    return null;
  }
}
