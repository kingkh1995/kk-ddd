package com.kkk.op.user.converter;

import com.kkk.op.support.bean.DataConvertSupport;
import com.kkk.op.support.marker.DataConverter;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.types.AccountStatus;
import com.kkk.op.user.enums.AccountStatusEnum;
import com.kkk.op.user.persistence.AccountDO;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;

/**
 * 使用Enum实现单例模式
 *
 * todo... 使用 mapstruct 实现赋值
 *
 * @author KaiKoo
 */
public enum AccountDataConverter implements DataConverter<Account, AccountDO> {

    INSTANCE;

    // 设置为 transient
    private final static transient DataConverter<Account, AccountDO> CONVERT_SUPPORT = new AccountDataConverterSupport();

    @Override
    public AccountDO toData(Account account) {
        return CONVERT_SUPPORT.toData(account);
    }

    @Override
    public Account fromData(AccountDO data) {
        return CONVERT_SUPPORT.fromData(data);
    }

    @Override
    public List<AccountDO> toData(Collection<Account> entityCol) {
        return CONVERT_SUPPORT.toData(entityCol);
    }

    @Override
    public List<Account> fromData(Collection<AccountDO> dataCol) {
        return CONVERT_SUPPORT.fromData(dataCol);
    }

    // 使用私有内部类实现
    private static class AccountDataConverterSupport extends
            DataConvertSupport<Account, AccountDO> {

        private AccountDataConverterSupport() {
            // 防止利用反射机制调用
            if (CONVERT_SUPPORT != null) {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected AccountDO buildDataFromEntity(@NotNull Account account) {
            var data = new AccountDO();
            data.setId(Optional.ofNullable(account.getId()).map(LongId::getValue).orElse(null));
            data.setUserId(
                    Optional.ofNullable(account.getUserId()).map(LongId::getValue).orElse(null));
            data.setStatus(Optional.ofNullable(account.getStatus()).map(AccountStatus::getValue)
                    .map(AccountStatusEnum::name).orElse(null));
            return data;
        }

        @Override
        protected Account buildEntityFromData(@NotNull AccountDO data) {
            var builder = Account.builder();
            builder.id(Optional.ofNullable(data.getId()).map(LongId::new).orElse(null))
                    .userId(Optional.ofNullable(data.getUserId()).map(LongId::new).orElse(null))
                    .status(new AccountStatus(data.getStatus()));
            return builder.build();
        }
    }
}
