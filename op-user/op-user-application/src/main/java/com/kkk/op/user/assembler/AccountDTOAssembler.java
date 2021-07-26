package com.kkk.op.user.assembler;

import com.kkk.op.support.enums.AccountStatusEnum;
import com.kkk.op.support.marker.DTOAssembler;
import com.kkk.op.support.models.dto.AccountDTO;
import com.kkk.op.support.tools.DateUtil;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.types.AccountId;
import com.kkk.op.user.domain.types.AccountStatus;
import java.util.Optional;

/**
 * 使用Enum实现单例模式 <br>
 * todo... 使用 mapstruct 实现赋值
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
    Optional.ofNullable(account.getStatus())
        .map(AccountStatus::getValue)
        .map(AccountStatusEnum::name)
        .ifPresent(dto::setStatus);
    Optional.ofNullable(account.getCreateTime())
        .map(DateUtil::toEpochMilli)
        .ifPresent(dto::setCreateTime);
    return dto;
  }

  @Override
  public Account fromDTO(AccountDTO dto) {
    // Entity是有行为的，所以需要保证不能返回null
    var builder = Account.builder();
    Optional.ofNullable(dto)
        .ifPresent(
            accountDTO -> {
              Optional.ofNullable(accountDTO.getId()).map(AccountId::valueOf).ifPresent(builder::id);
              Optional.ofNullable(accountDTO.getUserId())
                  .map(userId -> LongId.valueOf(userId, "userId"))
                  .ifPresent(builder::userId);
              Optional.ofNullable(accountDTO.getStatus())
                  .filter(s -> !s.isBlank())
                  .map(status -> AccountStatus.from(status))
                  .ifPresent(builder::status);
              Optional.ofNullable(accountDTO.getCreateTime())
                  .map(DateUtil::toLocalDateTime)
                  .ifPresent(builder::createTime);
            });
    return builder.build();
  }
}
