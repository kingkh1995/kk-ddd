package com.kkk.op.user.repository.impl;

import com.kkk.op.support.base.EntityRepositorySupport;
import com.kkk.op.support.exception.BusinessException;
import com.kkk.op.user.converter.AccountConverter;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.type.AccountId;
import com.kkk.op.user.domain.type.UserId;
import com.kkk.op.user.persistence.AccountMapper;
import com.kkk.op.user.repository.AccountRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Entity类Repository实现类 （仅模板，实际上聚合根子实体不应该单独存在）
 *
 * @author KaiKoo
 */
@Component
@RequiredArgsConstructor
public class AccountRepositoryImpl extends EntityRepositorySupport<Account, AccountId>
    implements AccountRepository {

  private final AccountConverter accountConverter = AccountConverter.INSTANCE;

  private final AccountMapper accountMapper;

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
  protected List<Account> onSelectByIds(@NotEmpty Set<AccountId> accountIds) {
    return accountConverter.fromData(
        accountMapper.selectByIds(
            accountIds.stream()
                .mapToLong(AccountId::getValue)
                .boxed()
                .collect(Collectors.toSet())));
  }

  @Override
  public List<Account> find(UserId userId) {
    return accountConverter.fromData(accountMapper.selectByUserId(userId.getValue()));
  }
}
