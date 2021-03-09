package com.kkk.op.user.assembler;

import com.kkk.op.support.models.dto.AccountDTO;
import com.kkk.op.support.type.LongId;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.entity.Account.AccountBuilder;
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
        AccountBuilder builder = Account.builder();
        builder.id(Optional.ofNullable(dto.getId()).map(LongId::new).orElse(null))
                .userId(Optional.ofNullable(dto.getUserId()).map(LongId::new).orElse(null));
        return builder.build();
    }

    public AccountDTO toDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(Optional.ofNullable(account.getId()).map(LongId::getValue).orElse(null));
        dto.setUserId(Optional.ofNullable(account.getUserId()).map(LongId::getValue).orElse(null));
        return dto;
    }

}
