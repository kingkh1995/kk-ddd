package com.kkk.op.user.domain.types;

import com.kkk.op.support.marker.Type;
import com.kkk.op.user.domain.enums.AccountStatusEnum;
import javax.validation.ValidationException;

/**
 * 枚举值也封装为DP
 * @author KaiKoo
 */
public class AccountStatus implements Type {

    private AccountStatusEnum statusEnum;

    public AccountStatus(String status) {
        if (status == null || status.strip().isEmpty()) {
            throw new ValidationException("status不能为空");
        }
        var statusEnum = AccountStatusEnum.valueOf(status);
        if (statusEnum == null) {
            throw new ValidationException("status值不合法");
        }
        this.statusEnum = statusEnum;
    }

    public AccountStatus(AccountStatusEnum statusEnum) {
        if (statusEnum == null) {
            throw new ValidationException("statusEnum不能为空");
        }
        this.statusEnum = statusEnum;
    }

    public AccountStatusEnum getValue() {
        return this.statusEnum;
    }
}
