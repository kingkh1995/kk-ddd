package com.kkk.op.user.domain.types;

import com.kkk.op.support.enums.AccountStatusEnum;
import com.kkk.op.support.exception.IllegalArgumentExceptions;
import com.kkk.op.support.marker.Type;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 枚举值也封装为DP
 * @author KaiKoo
 */
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountStatus implements Type {

    @Getter
    protected final AccountStatusEnum value;

    public static AccountStatus valueOf(String s) {
        return valueOf(s, null);
    }

    public static AccountStatus valueOf(String s, String prefix) {
        if (s == null || s.isBlank()) {
            throw IllegalArgumentExceptions.forNull(prefix);
        }
        // valueOf 方法不会返回 null，会抛出异常
        try {
            return new AccountStatus(AccountStatusEnum.valueOf(s));
        } catch (IllegalArgumentException e) {
            throw IllegalArgumentExceptions.forInvalidEnum(prefix);
        }
    }

    public static AccountStatus valueOf(AccountStatusEnum accountStatusEnum) {
        return valueOf(accountStatusEnum, null);
    }

    public static AccountStatus valueOf(AccountStatusEnum accountStatusEnum, String prefix) {
        if (accountStatusEnum == null) {
            throw IllegalArgumentExceptions.forNull(prefix);
        }
        return new AccountStatus(accountStatusEnum);
    }

}
