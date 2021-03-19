package com.kkk.op.user.query.entity;

import com.kkk.op.support.bean.AbstractQuery;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.service.AccountService;
import com.kkk.op.user.domain.types.AccountStatus;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 查询实体
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@SuperBuilder
public class AccountQuery extends AbstractQuery {

    @Setter//todo... 删除
    private LongId id;

    private LongId userId;

    private AccountStatus status;

    public Account find(AccountService accountService) {
        // find by id
        if (this.id != null) {
            return accountService.find(this.id);
        }
        // todo... find by fields
        return null;
    }

    public List<Account> list(AccountService accountService) {
        // todo... list by fields
        return Collections.EMPTY_LIST;
    }
}
