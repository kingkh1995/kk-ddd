package com.kkk.op.user.query.entity;

import com.kkk.op.support.bean.AbstractQuery;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.service.AccountService;
import com.kkk.op.user.domain.types.AccountStatus;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
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

    private LongId id;

    private LongId userId;

    private AccountStatus status;

    private Set<LongId> ids;

    // 日期查询区间开始（包含）
    private LocalDateTime createTimeStart;

    // 日期查询区间结束（不包含）
    private LocalDateTime createTimeEnd;

    public Account find(AccountService accountService) {
        // find by id
        if (this.id != null) {
            return accountService.find(this.id);
        }
        // todo... find by fields
        return null;
    }

    public List<Account> list(AccountService accountService) {
        System.out.println(this.toString());
        // todo... list by fields
        return Collections.EMPTY_LIST;
    }
}
