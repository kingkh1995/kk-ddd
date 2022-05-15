package com.kkk.op.user.converter;

import com.kkk.op.support.enums.AccountStateEnum;
import com.kkk.op.support.marker.DataConverter;
import com.kkk.op.support.type.Version;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.type.AccountId;
import com.kkk.op.user.domain.type.AccountState;
import com.kkk.op.user.domain.type.UserId;
import com.kkk.op.user.persistence.AccountDO;
import java.util.Date;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * <br>
 *
 * @author KaiKoo
 */
public enum AccountConverter implements DataConverter<Account, AccountDO> {
  INSTANCE;

  @Override
  public AccountDO toData(Account account) {
    if (account == null) {
      return null;
    }
    var data = new AccountDO();
    Optional.ofNullable(account.getId()).map(AccountId::getValue).ifPresent(data::setId);
    Optional.ofNullable(account.getUserId()).map(UserId::getValue).ifPresent(data::setUserId);
    Optional.ofNullable(account.getState())
        .map(AccountState::getValue)
        .map(AccountStateEnum::name)
        .ifPresent(data::setState);
    Optional.ofNullable(account.getVersion()).map(Version::getValue).ifPresent(data::setVersion);
    Optional.ofNullable(account.getCreateTime()).map(Date::from).ifPresent(data::setCreateTime);
    Optional.ofNullable(account.getUpdateTime()).map(Date::from).ifPresent(data::setUpdateTime);
    return data;
  }

  @Override
  public Account fromData(AccountDO accountDO) {
    if (accountDO == null) {
      return null;
    }
    var builder = Account.builder();
    Optional.ofNullable(accountDO.getId()).map(AccountId::of).ifPresent(builder::id);
    Optional.ofNullable(accountDO.getUserId()).map(UserId::of).ifPresent(builder::userId);
    Optional.ofNullable(accountDO.getState())
        .filter(Predicate.not(String::isBlank))
        .map(AccountStateEnum::valueOf)
        .map(AccountState::of)
        .ifPresent(builder::state);
    Optional.ofNullable(accountDO.getVersion()).map(Version::of).ifPresent(builder::version);
    Optional.ofNullable(accountDO.getCreateTime())
        .map(Date::toInstant)
        .ifPresent(builder::createTime);
    Optional.ofNullable(accountDO.getUpdateTime())
        .map(Date::toInstant)
        .ifPresent(builder::updateTime);
    return builder.build();
  }
}
