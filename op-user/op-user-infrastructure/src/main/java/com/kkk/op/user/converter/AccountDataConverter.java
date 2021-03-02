package com.kkk.op.user.converter;

import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.Account;
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
        Account entity = new Account();
        entity.setId(Optional.ofNullable(accountDO.getId()).map(LongId::new).orElse(null));
        entity.setUserId(Optional.ofNullable(accountDO.getUserId()).map(LongId::new).orElse(null));
        return entity;
    }

    public AccountDO toData(Account account) {
        AccountDO data = new AccountDO();
        data.setId(Optional.ofNullable(account.getId()).map(LongId::getValue).orElse(null));
        data.setUserId(Optional.ofNullable(account.getUserId()).map(LongId::getValue).orElse(null));
        return data;
    }

}
