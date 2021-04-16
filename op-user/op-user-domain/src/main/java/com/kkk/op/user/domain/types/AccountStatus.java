package com.kkk.op.user.domain.types;

import com.kkk.op.support.enums.AccountStatusEnum;
import com.kkk.op.support.marker.Type;

/**
 * 枚举值也封装为DP
 * @author KaiKoo
 */
public class AccountStatus implements Type {

    private final AccountStatusEnum statusEnum;

    public AccountStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("status不能为空");
        }
        // valueOf 方法不会返回 null，会抛出异常
        try {
            this.statusEnum = AccountStatusEnum.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("不存在status对应的枚举值");
        }
    }

    public AccountStatus(AccountStatusEnum statusEnum) {
        if (statusEnum == null) {
            throw new IllegalArgumentException("statusEnum不能为空");
        }
        this.statusEnum = statusEnum;
    }

    public AccountStatusEnum getValue() {
        return this.statusEnum;
    }

}
