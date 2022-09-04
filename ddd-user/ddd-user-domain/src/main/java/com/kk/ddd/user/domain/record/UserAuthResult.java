package com.kk.ddd.user.domain.record;

import com.kk.ddd.user.domain.type.AccountId;
import com.kk.ddd.user.domain.type.AuthStrength;
import java.time.Instant;

/**
 * 用户验证结果 <br/>
 *
 * @author KaiKoo
 */
public record UserAuthResult(AccountId accountId,
                             AuthStrength strength,
                             Instant authAt,
                             Instant expiredAt) {

    public boolean hasExpired() {
        return expiredAt().isBefore(Instant.now());
    }
}
