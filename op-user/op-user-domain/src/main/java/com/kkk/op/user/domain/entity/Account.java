package com.kkk.op.user.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kkk.op.support.base.Entity;
import com.kkk.op.support.base.LocalRequestContextHolder;
import com.kkk.op.support.changeTracking.diff.DiffIgnore;
import com.kkk.op.support.enums.AccountStateEnum;
import com.kkk.op.support.exception.BusinessException;
import com.kkk.op.support.marker.NameGenerator;
import com.kkk.op.support.types.StampedTime;
import com.kkk.op.support.types.Version;
import com.kkk.op.user.domain.service.AccountService;
import com.kkk.op.user.domain.types.AccountId;
import com.kkk.op.user.domain.types.AccountState;
import com.kkk.op.user.domain.types.UserId;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户账号
 *
 * @author KaiKoo
 */
@JsonDeserialize(builder = Account.AccountBuilder.class) // 设置反序列化使用Builder
@EqualsAndHashCode(callSuper = true)
@Getter
@Builder
public class Account extends Entity<AccountId> {

  @Setter(AccessLevel.PROTECTED)
  private AccountId id;

  private UserId userId;

  private AccountState state;

  @DiffIgnore // 设置创建时间不参数对比
  private StampedTime createTime;

  @DiffIgnore private Version version;

  @Override
  public void validate() {
    // userId不能为null
    if (this.userId == null) {
      throw new IllegalArgumentException("userId不能为空");
    }
  }

  @Override
  public String generateLockName(NameGenerator lockNameGenerator) {
    return lockNameGenerator.generate(
        "user", "Account", Objects.requireNonNull(this.getId()).identifier());
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
      // 设置创建时间
      this.createTime = StampedTime.from(LocalRequestContextHolder.get().getCommitTime());
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
    var op = accountService.find(this.id);
    // 逻辑校验
    op.orElseThrow(() -> new BusinessException("不存在的id"));
    return op.get();
  }
}
