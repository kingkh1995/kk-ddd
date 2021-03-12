package com.kkk.op.user.assembler;

import com.kkk.op.support.models.user.AccountDTO;
import com.kkk.op.support.models.user.AccountQueryDTO;
import com.kkk.op.support.types.LongId;
import com.kkk.op.support.types.PageSize;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.enums.AccountStatusEnum;
import com.kkk.op.user.domain.types.AccountStatus;
import com.kkk.op.user.query.entity.AccountQuery;
import java.util.Optional;

/**
 * todo... 待优化
 * @author KaiKoo
 */
public class AccountDTOAssembler {

    //使用volatile解决双重检查问题
    private static volatile AccountDTOAssembler INSTANCE;

    //构造方法设置为私有
    private AccountDTOAssembler() {
    }

    public static AccountDTOAssembler getInstance() {
        if (INSTANCE == null) {
            synchronized (AccountDTOAssembler.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AccountDTOAssembler();
                }
            }
        }
        return INSTANCE;
    }

    public Account fromDTO(AccountDTO dto) {
        if (dto == null) {
            return null;
        }
        var builder = Account.builder();
        builder.id(Optional.ofNullable(dto.getId()).map(LongId::new).orElse(null))
                .userId(Optional.ofNullable(dto.getUserId()).map(LongId::new).orElse(null));
        return builder.build();
    }

    public AccountDTO toDTO(Account account) {
        if (account == null) {
            return null;
        }
        var dto = new AccountDTO();
        dto.setId(Optional.ofNullable(account.getId()).map(LongId::getValue).orElse(null));
        dto.setUserId(Optional.ofNullable(account.getUserId()).map(LongId::getValue).orElse(null));
        dto.setStatus(Optional.ofNullable(account.getStatus()).map(AccountStatus::getValue)
                .map(AccountStatusEnum::name).orElse(null));
        return dto;
    }

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

}
