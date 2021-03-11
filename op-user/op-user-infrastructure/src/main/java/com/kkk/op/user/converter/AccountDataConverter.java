package com.kkk.op.user.converter;

import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.enums.AccountStatusEnum;
import com.kkk.op.user.domain.types.AccountStatus;
import com.kkk.op.user.persistence.AccountDO;
import java.util.Optional;

/**
 * todo... 待优化
 * @author KaiKoo
 */
public class AccountDataConverter {

    //使用volatile解决双重检查问题
    private static volatile AccountDataConverter INSTANCE;

    //构造方法设置为私有
    private AccountDataConverter() {
    }

    public static AccountDataConverter getInstance() {
        if (INSTANCE == null) {
            synchronized (AccountDataConverter.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AccountDataConverter();
                }
            }
        }
        return INSTANCE;
    }

    public Account fromData(AccountDO accountDO) {
        if (accountDO == null) {
            return null;
        }
        var builder = Account.builder();
        builder.id(Optional.ofNullable(accountDO.getId()).map(LongId::new).orElse(null))
                .userId(Optional.ofNullable(accountDO.getUserId()).map(LongId::new).orElse(null))
                .status(new AccountStatus(accountDO.getStatus()));
        return builder.build();
    }

    public AccountDO toData(Account account) {
        if (account == null) {
            return null;
        }
        var data = new AccountDO();
        data.setId(Optional.ofNullable(account.getId()).map(LongId::getValue).orElse(null));
        data.setUserId(Optional.ofNullable(account.getUserId()).map(LongId::getValue).orElse(null));
        data.setStatus(Optional.ofNullable(account.getStatus()).map(AccountStatus::getValue)
                .map(AccountStatusEnum::name).orElse(null));
        return data;
    }

}
