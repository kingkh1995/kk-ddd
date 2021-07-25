package com.kkk.op.user.domain.entity;

import com.kkk.op.support.base.Entity;
import com.kkk.op.support.enums.AccountStatusEnum;
import com.kkk.op.support.exception.BussinessException;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.service.AccountService;
import com.kkk.op.user.domain.types.AccountId;
import com.kkk.op.user.domain.types.AccountStatus;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户账号
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
@ToString
@Getter
@Builder
public class Account extends Entity<AccountId> {

  @Setter(AccessLevel.PROTECTED)
  private AccountId id;

  private LongId userId;

  private AccountStatus status;

  private LocalDateTime createTime;

  @Override
  public Account snapshot() {
    return builder().id(this.id).userId(this.userId).status(this.status).build();
  }

  @Override
  public void validate() {
    // userId不能为null
    if (this.userId == null) {
      throw new IllegalArgumentException("userId不能为空");
    }
  }

  public void remove(AccountService accountService) {
    this.checkIdExist(accountService);
    // todo... 业务逻辑 & 变更状态
    accountService.remove(this);
  }

  public void save(AccountService accountService) {
    // validate
    this.validate();
    // handle
    if (this.id == null) {
      // 新增逻辑
      // 设置初始状态
      this.status = AccountStatus.of(AccountStatusEnum.INIT);
      this.createTime = LocalDateTime.now();
    } else {
      // 更新逻辑
      var oldAccount = this.checkIdExist(accountService);
      if (!accountService.allowModify(oldAccount, this)) {
        throw new BussinessException("不允许修改");
      }
    }
    // save
    accountService.save(this);
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
