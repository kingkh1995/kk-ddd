package com.kk.ddd.user.converter;

import com.kk.ddd.support.core.DataConverter;
import com.kk.ddd.support.enums.AccountStateEnum;
import com.kk.ddd.support.type.Version;
import com.kk.ddd.user.domain.entity.Account;
import com.kk.ddd.user.domain.type.AccountId;
import com.kk.ddd.user.domain.type.AccountState;
import com.kk.ddd.user.domain.type.UserId;
import com.kk.ddd.user.persistence.AccountPO;
import java.util.Date;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * <br>
 *
 * @author KaiKoo
 */
public enum AccountConverter implements DataConverter<Account, AccountPO> {
  INSTANCE;

  @Override
  public AccountPO toData(Account account) {
    if (account == null) {
      return null;
    }
    var data = new AccountPO();
    Optional.ofNullable(account.getId()).map(AccountId::getValue).ifPresent(data::setId);
    Optional.ofNullable(account.getUserId()).map(UserId::getValue).ifPresent(data::setUserId);
    Optional.ofNullable(account.getState())
        .map(AccountState::toEnum)
        .map(AccountStateEnum::name)
        .ifPresent(data::setState);
    Optional.ofNullable(account.getVersion()).map(Version::getValue).ifPresent(data::setVersion);
    Optional.ofNullable(account.getCreateTime()).map(Date::from).ifPresent(data::setCreateTime);
    Optional.ofNullable(account.getUpdateTime()).map(Date::from).ifPresent(data::setUpdateTime);
    return data;
  }

  @Override
  public Account fromData(AccountPO accountPO) {
    if (accountPO == null) {
      return null;
    }
    var builder = Account.builder();
    Optional.ofNullable(accountPO.getId()).map(AccountId::of).ifPresent(builder::id);
    Optional.ofNullable(accountPO.getUserId()).map(UserId::of).ifPresent(builder::userId);
    Optional.ofNullable(accountPO.getState())
        .filter(Predicate.not(String::isBlank))
        .map(AccountStateEnum::valueOf)
        .map(AccountState::of)
        .ifPresent(builder::state);
    Optional.ofNullable(accountPO.getVersion()).map(Version::of).ifPresent(builder::version);
    Optional.ofNullable(accountPO.getCreateTime())
        .map(Date::toInstant)
        .ifPresent(builder::createTime);
    Optional.ofNullable(accountPO.getUpdateTime())
        .map(Date::toInstant)
        .ifPresent(builder::updateTime);
    return builder.build();
  }
}
