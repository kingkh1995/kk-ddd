package com.kkk.op.user.converter;

import com.kkk.op.support.enums.AccountStatusEnum;
import com.kkk.op.support.marker.DataConverter;
import com.kkk.op.support.tools.DateUtil;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.types.AccountStatus;
import com.kkk.op.user.persistence.model.AccountDO;
import java.util.Optional;

/**
 * <br>
 *
 * @author KaiKoo
 */
public enum AccountDataConverter implements DataConverter<Account, AccountDO> {
  INSTANCE;

  @Override
  public AccountDO toData(Account account) {
    if (account == null) {
      return null;
    }
    var data = new AccountDO();
    Optional.ofNullable(account.getId()).map(LongId::longValue).ifPresent(data::setId);
    Optional.ofNullable(account.getUserId()).map(LongId::longValue).ifPresent(data::setUserId);
    Optional.ofNullable(account.getStatus())
        .map(AccountStatus::getValue)
        .map(AccountStatusEnum::name)
        .ifPresent(data::setStatus);
    Optional.ofNullable(account.getCreateTime())
        .map(DateUtil::toTimestamp)
        .ifPresent(data::setCreateTime);
    return data;
  }

  @Override
  public Account fromData(AccountDO data) {
    var builder = Account.builder();
    Optional.ofNullable(data)
        .ifPresent(
            accountDO -> {
              Optional.ofNullable(accountDO.getId()).map(LongId::of).ifPresent(builder::id);
              Optional.ofNullable(accountDO.getUserId()).map(LongId::of).ifPresent(builder::userId);
              Optional.ofNullable(accountDO.getStatus())
                  .filter(s -> !s.isBlank())
                  .map(AccountStatusEnum::valueOf)
                  .map(AccountStatus::of)
                  .ifPresent(builder::status);
              Optional.ofNullable(accountDO.getCreateTime())
                  .map(DateUtil::toLocalDateTime)
                  .ifPresent(builder::createTime);
            });
    return builder.build();
  }
}
