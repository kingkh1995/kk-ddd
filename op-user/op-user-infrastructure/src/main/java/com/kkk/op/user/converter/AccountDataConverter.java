package com.kkk.op.user.converter;

import com.kkk.op.support.enums.AccountStatusEnum;
import com.kkk.op.support.marker.DataConverter;
import com.kkk.op.support.tools.DateUtil;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.types.AccountStatus;
import com.kkk.op.user.persistence.AccountDO;
import java.util.Optional;

/**
 *
 * @author KaiKoo
 */
public enum AccountDataConverter implements DataConverter<Account, AccountDO> {

    INSTANCE;

    @Override
    public AccountDO toData(Account account) {
        if (account == null) {
            return null;
        }
        var data = new AccountDO();
        data.setId(Optional.ofNullable(account.getId()).map(LongId::getId).orElse(null));
        data.setUserId(
                Optional.ofNullable(account.getUserId()).map(LongId::getId).orElse(null));
        data.setStatus(Optional.ofNullable(account.getStatus()).map(AccountStatus::getValue)
                .map(AccountStatusEnum::name).orElse(null));
        data.setCreateTime(Optional.ofNullable(account.getCreateTime()).map(DateUtil::toTimestamp)
                .orElse(null));
        return data;
    }

    @Override
    public Account fromData(AccountDO data) {
        var builder = Account.builder();
        if (data != null) {
            builder.id(Optional.ofNullable(data.getId()).map(LongId::new).orElse(null))
                    .userId(Optional.ofNullable(data.getUserId()).map(LongId::new).orElse(null))
                    .status(Optional.ofNullable(data.getStatus()).filter(s -> !s.isBlank())
                            .map(AccountStatus::new).orElse(null))
                    .createTime(
                            Optional.ofNullable(data.getCreateTime()).map(DateUtil::toLocalDateTime)
                                    .orElse(null));
        }
        return builder.build();
    }
}
