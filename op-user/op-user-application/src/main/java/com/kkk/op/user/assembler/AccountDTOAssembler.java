package com.kkk.op.user.assembler;

import com.kkk.op.support.bean.DTOAssemblerSupport;
import com.kkk.op.support.marker.DTOAssembler;
import com.kkk.op.support.models.user.AccountDTO;
import com.kkk.op.support.models.user.AccountQueryDTO;
import com.kkk.op.support.types.LongId;
import com.kkk.op.support.types.PageSize;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.types.AccountStatus;
import com.kkk.op.user.enums.AccountStatusEnum;
import com.kkk.op.user.query.entity.AccountQuery;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;

/**
 *
 * @author KaiKoo
 */
public enum AccountDTOAssembler implements DTOAssembler<Account, AccountDTO> {

    INSTANCE;

    private final static transient DTOAssembler<Account, AccountDTO> ASSEMBLER_SUPPORT = new AccountDTOAssemblerSupport();

    /**
     * 以下为query类转换方法
     */
    public AccountQuery toQuery(AccountQueryDTO dto) {
        if (dto == null) {
            return null;
        }
        var builder = AccountQuery.builder();
        builder.id(Optional.ofNullable(dto.getId()).map(LongId::new).orElse(null))
                .userId(Optional.ofNullable(dto.getUserId()).map(LongId::new).orElse(null))
                .status(Optional.ofNullable(dto.getStatus()).map(AccountStatus::new).orElse(null))
                .size(new PageSize(dto.getSize()));
        return builder.build();
    }

    @Override
    public AccountDTO toDTO(Account account) {
        return ASSEMBLER_SUPPORT.toDTO(account);
    }

    @Override
    public Account fromDTO(AccountDTO dto) {
        return ASSEMBLER_SUPPORT.fromDTO(dto);
    }

    @Override
    public List<AccountDTO> toDTO(Collection<Account> entityCol) {
        return ASSEMBLER_SUPPORT.toDTO(entityCol);
    }

    @Override
    public List<Account> fromDTO(Collection<AccountDTO> dtoCol) {
        return ASSEMBLER_SUPPORT.fromDTO(dtoCol);
    }

    private static class AccountDTOAssemblerSupport extends
            DTOAssemblerSupport<Account, AccountDTO> {

        private AccountDTOAssemblerSupport() {
            if (ASSEMBLER_SUPPORT != null) {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected AccountDTO buildDTOFromEntity(@NotNull Account account) {
            var dto = new AccountDTO();
            dto.setId(Optional.ofNullable(account.getId()).map(LongId::getValue).orElse(null));
            dto.setUserId(
                    Optional.ofNullable(account.getUserId()).map(LongId::getValue).orElse(null));
            dto.setStatus(Optional.ofNullable(account.getStatus()).map(AccountStatus::getValue)
                    .map(AccountStatusEnum::name).orElse(null));
            return dto;
        }

        @Override
        protected Account buildEntityFromDTO(@NotNull AccountDTO dto) {
            var builder = Account.builder();
            builder.id(Optional.ofNullable(dto.getId()).map(LongId::new).orElse(null))
                    .userId(Optional.ofNullable(dto.getUserId()).map(LongId::new).orElse(null));
            return builder.build();
        }
    }
}
