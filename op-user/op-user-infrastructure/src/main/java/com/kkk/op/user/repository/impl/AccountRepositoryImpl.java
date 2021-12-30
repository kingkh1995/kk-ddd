package com.kkk.op.user.repository.impl;

import com.kkk.op.support.base.EntityRepositorySupport;
import com.kkk.op.support.exception.BusinessException;
import com.kkk.op.user.converter.AccountConverter;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.types.AccountId;
import com.kkk.op.user.persistence.mapper.AccountMapper;
import com.kkk.op.user.repository.AccountRepository;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
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

  private final AccountConverter accountConverter = AccountConverter.INSTANCE;

  // 并没有创建线程池，而是通过内部类Delayer延迟提交任务到执行线程池。
  private static final Executor DELAYED_EXECUTOR =
      CompletableFuture.delayedExecutor(2L, TimeUnit.SECONDS);

  private final AccountMapper accountMapper;

  public AccountRepositoryImpl(final AccountMapper accountMapper) {
    super(Account.class);
    this.accountMapper = accountMapper;
  }

  @Override
  public void cacheDelayRemoveAsync(AccountId accountId) {
    DELAYED_EXECUTOR.execute(() -> cacheRemove(accountId));
  }

  @Override
  protected void onInsert(@NotNull Account entity) {
    var data = accountConverter.toData(entity);
    accountMapper.insert(data);
    // 填补id
    entity.fillInId(AccountId.from(data.getId()));
  }

  @Override
  protected void onUpdate(@NotNull Account entity) {
    if (accountMapper.updateById(accountConverter.toData(entity)) < 1) {
      throw new BusinessException("Update failed by OCC!");
    }
  }

  @Override
  protected void onDelete(@NotNull Account entity) {
    // todo... 逻辑删除 LD（logic delete） & 乐观锁 OCC （optimistic concurrency control）
    accountMapper.deleteById(entity.getId().getValue());
  }

  @Override
  protected Optional<Account> onSelect(@NotNull AccountId accountId) {
    return accountMapper.selectById(accountId.getValue()).map(accountConverter::fromData);
  }

  @Override
  protected Map<AccountId, Account> onSelectByIds(@NotEmpty Set<AccountId> accountIds) {
    return accountMapper
        .selectByIds(
            accountIds.stream().mapToLong(AccountId::getValue).boxed().collect(Collectors.toSet()))
        .stream()
        .map(accountConverter::fromData)
        .collect(Collectors.toMap(Account::getId, Function.identity()));
  }
}
