package com.kkk.op.user.domain.entity;

import com.kkk.op.support.base.Entity;
import com.kkk.op.support.changeTracking.diff.DiffIgnore;
import com.kkk.op.support.enums.AccountStateEnum;
import com.kkk.op.support.exception.BusinessException;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.service.AccountService;
import com.kkk.op.user.domain.types.AccountId;
import com.kkk.op.user.domain.types.AccountState;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户账号
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 供builder使用
@NoArgsConstructor(access = AccessLevel.PRIVATE) // 供反序列化使用
public class Account extends Entity<AccountId> {

  @Setter(AccessLevel.PROTECTED)
  private AccountId id;

  private LongId userId;

  private AccountState state;

  @DiffIgnore // 设置创建时间不参数对比
  private LocalDateTime createTime;

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
      this.state = AccountState.from(AccountStateEnum.INIT);
      this.createTime = LocalDateTime.now();
    } else {
      // 更新逻辑
      var oldAccount = this.checkIdExist(accountService);
      if (!accountService.allowModify(oldAccount, this)) {
        throw new BusinessException("不允许修改");
      }
    }
    // save
    accountService.save(this);
  }

  private Account checkIdExist(AccountService accountService) {
    Account account = accountService.find(this.id);
    // 逻辑校验
    if (account == null) {
      throw new BusinessException("id不存在");
    }
    return account;
  }
}
