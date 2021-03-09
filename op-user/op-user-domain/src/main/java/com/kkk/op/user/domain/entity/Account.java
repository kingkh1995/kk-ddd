package com.kkk.op.user.domain.entity;

import com.kkk.op.support.marker.Entity;
import com.kkk.op.support.type.LongId;
import com.kkk.op.user.domain.service.AccountService;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 用户账号
 * @author KaiKoo
 */
@EqualsAndHashCode
@ToString
@Getter
@Builder
public class Account implements Entity<LongId> {

    private LongId id;

    private LongId userId;

    @Override
    public Account snapshot() {
        return this.builder().id(this.id).userId(this.userId).build();
    }

    public Account find(AccountService accountService) {
        return accountService.find(this.getId());
    }

    public void remove(AccountService accountService) {
        accountService.remove(this);
    }

    public void save(AccountService accountService) {
        accountService.save(this);
    }
}
