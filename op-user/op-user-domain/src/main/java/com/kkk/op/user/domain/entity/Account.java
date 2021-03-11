package com.kkk.op.user.domain.entity;

import com.kkk.op.support.exception.BussinessException;
import com.kkk.op.support.marker.Entity;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.enums.AccountStatusEnum;
import com.kkk.op.user.domain.service.AccountService;
import com.kkk.op.user.domain.types.AccountStatus;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
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

    @Setter//todo... 删除
    private LongId id;

    private LongId userId;

    private AccountStatus status;

    @Override
    public Account snapshot() {
        return this.builder().id(this.id).userId(this.userId).status(this.status).build();
    }

    public void remove(AccountService accountService) {
        checkIdExist(accountService);
        // todo... 变更操作
        accountService.remove(this);
    }

    public void save(AccountService accountService) {
        if (this.id == null) {
            // 新增逻辑
            // 设置初始状态
            this.status = new AccountStatus(AccountStatusEnum.INIT);
        } else {
            // 更新逻辑
            var oldAccount = checkIdExist(accountService);
            if (!accountService.allowModify(oldAccount, this)) {
                throw new BussinessException("不允许修改");
            }
        }
        var id = accountService.save(this);
        // 回填Id
        this.id = id;
    }

    private Account checkIdExist(AccountService accountService) {
        Account account = accountService.find(this.id);
        // 逻辑校验
        if (account == null) {
            throw new BussinessException("id不存在");
        }
        return account;
    }
}
