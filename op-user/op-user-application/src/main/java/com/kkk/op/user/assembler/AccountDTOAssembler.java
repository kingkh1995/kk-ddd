package com.kkk.op.user.assembler;

import com.kkk.op.support.enums.AccountStatusEnum;
import com.kkk.op.support.marker.DTOAssembler;
import com.kkk.op.support.models.user.AccountDTO;
import com.kkk.op.support.models.user.AccountQueryDTO;
import com.kkk.op.support.tools.DateUtil;
import com.kkk.op.support.types.LongId;
import com.kkk.op.support.types.PageSize;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.types.AccountStatus;
import com.kkk.op.user.query.entity.AccountQuery;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 使用Enum实现单例模式
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
        dto.setUserId(
                Optional.ofNullable(account.getUserId()).map(LongId::getValue).orElse(null));
        dto.setStatus(Optional.ofNullable(account.getStatus()).map(AccountStatus::getValue)
                .map(AccountStatusEnum::name).orElse(null));
        dto.setCreateTime(Optional.ofNullable(account.getCreateTime()).map(DateUtil::toEpochSecond)
                .orElse(null));
        return dto;
    }

    @Override
    public Account fromDTO(AccountDTO dto) {
        // Entity是有行为的，所以需要保证不能返回null
        var builder = Account.builder();
        if (dto != null) {
            builder.id(Optional.ofNullable(dto.getId()).map(LongId::valueOf).orElse(null))
                    .userId(Optional.ofNullable(dto.getUserId()).map(LongId::valueOf).orElse(null))
                    .status(Optional.ofNullable(dto.getStatus()).filter(s -> !s.isBlank())
                            .map(AccountStatus::new).orElse(null))
                    .createTime(
                            Optional.ofNullable(dto.getCreateTime()).map(DateUtil::toLocalDateTime)
                                    .orElse(null));
        }
        return builder.build();
    }

    /**
     * 以下为query类转换方法
     */
    public AccountQuery toQuery(AccountQueryDTO accountQueryDTO) {
        // Query是有行为的，所以需要保证不能返回null
        var builder = AccountQuery.builder();
        if (accountQueryDTO != null) {
            builder.id(Optional.ofNullable(accountQueryDTO.getId()).map(LongId::valueOf).orElse(null))
                    .userId(Optional.ofNullable(accountQueryDTO.getUserId()).map(LongId::valueOf)
                            .orElse(null)).status(Optional.ofNullable(accountQueryDTO.getStatus())
                    .filter(s -> !s.isBlank()).map(AccountStatus::new).orElse(null))
                    .size(Optional.ofNullable(accountQueryDTO.getSize()).map(PageSize::valueOf)
                            .orElse(null))
                    .ids(Optional.ofNullable(accountQueryDTO.getIds())
                            .map(array -> Arrays.stream(array).filter(Objects::nonNull)
                                    .collect(Collectors.toSet())).orElse(Collections.EMPTY_SET))
                    .createTimeStart(accountQueryDTO.getDatePattern()
                            .parse(accountQueryDTO.getCreateTimeStart()))
                    .createTimeEnd(accountQueryDTO.getDatePattern()
                            .parse(accountQueryDTO.getCreateTimeEnd()));
        }
        return builder.build();
    }

}
