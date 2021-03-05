package com.kkk.op.user.domain.entity;

import com.kkk.op.support.marker.Entity;
import com.kkk.op.support.types.LongId;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户账号
 * @author KaiKoo
 */
@Data
@Accessors(chain = true)
public class Account implements Entity<LongId> {

    private LongId id;

    private LongId userId;

    @Override
    public Account snapshot() {
        Account snapshot = new Account();
        snapshot.setId(this.id)
                .setUserId(this.userId);
        return snapshot;
    }

}
