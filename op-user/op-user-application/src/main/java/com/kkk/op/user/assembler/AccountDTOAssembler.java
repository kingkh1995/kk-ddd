package com.kkk.op.user.assembler;

import com.kkk.op.support.enums.AccountStatusEnum;
import com.kkk.op.support.marker.DTOAssembler;
import com.kkk.op.support.models.dto.AccountDTO;
import com.kkk.op.support.tools.DateUtil;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.Account;
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
    dto.setId(Optional.ofNullable(account.getId()).map(LongId::getValue).orElse(null));
    dto.setUserId(Optional.ofNullable(account.getUserId()).map(LongId::getValue).orElse(null));
    dto.setStatus(
        Optional.ofNullable(account.getStatus())
            .map(AccountStatus::getValue)
            .map(AccountStatusEnum::name)
            .orElse(null));
    dto.setCreateTime(
        Optional.ofNullable(account.getCreateTime()).map(DateUtil::toEpochMilli).orElse(null));
    return dto;
  }

  @Override
  public Account fromDTO(AccountDTO dto) {
    // Entity是有行为的，所以需要保证不能返回null
    var builder = Account.builder();
    if (dto != null) {
      builder
          .id(Optional.ofNullable(dto.getId()).map(LongId::valueOf).orElse(null))
          .userId(Optional.ofNullable(dto.getUserId()).map(LongId::valueOf).orElse(null))
          .status(
              Optional.ofNullable(dto.getStatus())
                  .filter(s -> !s.isBlank())
                  .map(AccountStatus::valueOf)
                  .orElse(null))
          .createTime(
              Optional.ofNullable(dto.getCreateTime()).map(DateUtil::toLocalDateTime).orElse(null));
    }
    return builder.build();
  }
}
