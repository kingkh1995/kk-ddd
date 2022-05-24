package com.kkk.op.user.converter;

import com.kkk.op.support.enums.AccountStateEnum;
import com.kkk.op.support.marker.DataConverter;
import com.kkk.op.support.type.Version;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.type.AccountId;
import com.kkk.op.user.domain.type.AccountState;
import com.kkk.op.user.domain.type.UserId;
import com.kkk.op.user.persistence.AccountPO;
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
        .map(AccountState::getValue)
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
