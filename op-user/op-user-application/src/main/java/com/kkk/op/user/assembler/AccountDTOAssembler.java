package com.kkk.op.user.assembler;

import com.kkk.op.support.enums.AccountStateEnum;
import com.kkk.op.support.marker.DTOAssembler;
import com.kkk.op.support.model.dto.AccountDTO;
import com.kkk.op.support.tool.DateUtil;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.types.AccountId;
import com.kkk.op.user.domain.types.AccountState;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * 使用Enum实现单例模式 <br>
 *
 * @author KaiKoo
 */
public enum AccountDTOAssembler implements DTOAssembler<Account, AccountDTO> {

  // 设置一个唯一的枚举值保证单例模式
  INSTANCE;

  @Override
  public AccountDTO toDTO(Account account) {
    if (account == null) {
      return null;
    }
    var dto = new AccountDTO();
    Optional.ofNullable(account.getId()).map(AccountId::longValue).ifPresent(dto::setId);
    Optional.ofNullable(account.getUserId()).map(LongId::longValue).ifPresent(dto::setUserId);
    Optional.ofNullable(account.getState())
        .map(AccountState::getValue)
        .map(AccountStateEnum::name)
        .ifPresent(dto::setState);
    Optional.ofNullable(account.getCreateTime())
        .map(DateUtil::toEpochMilli)
        .ifPresent(dto::setCreateTime);
    return dto;
  }

  @Override
  public Account fromDTO(AccountDTO accountDTO) {
    if (accountDTO == null) {
      return null;
    }
    var builder = Account.builder();
    Optional.ofNullable(accountDTO.getId()).map(AccountId::from).ifPresent(builder::id);
    Optional.ofNullable(accountDTO.getUserId()).map(LongId::from).ifPresent(builder::userId);
    Optional.ofNullable(accountDTO.getState())
        .filter(Predicate.not(String::isBlank))
        .map(AccountStateEnum::valueOf)
        .map(AccountState::from)
        .ifPresent(builder::state);
    Optional.ofNullable(accountDTO.getCreateTime())
        .map(DateUtil::toLocalDateTime)
        .ifPresent(builder::createTime);
    return builder.build();
  }
}
