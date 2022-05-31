package com.kk.ddd.user.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kk.ddd.support.base.Entity;
import com.kk.ddd.support.enums.AccountStateEnum;
import com.kk.ddd.support.exception.BusinessException;
import com.kk.ddd.support.tracking.diff.DiffIgnore;
import com.kk.ddd.support.type.Version;
import com.kk.ddd.support.util.NameGenerator;
import com.kk.ddd.user.domain.service.AccountService;
import com.kk.ddd.user.domain.type.AccountId;
import com.kk.ddd.user.domain.type.AccountState;
import com.kk.ddd.user.domain.type.UserId;
import java.time.Instant;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户账户
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

  // Entity中使用Instant对应DO中的Date类型
  @DiffIgnore private Instant createTime;

  @DiffIgnore private Instant updateTime;

  private UserId userId;

  private AccountState state;

  // 版本号、创建时间、更新时间不参数对比
  @DiffIgnore private Version version;

  @Override
  public String generateLockName(NameGenerator lockNameGenerator) {
    return lockNameGenerator.generate(
        "user", "Account", Objects.requireNonNull(this.getId()).identifier());
  }

  public void remove(AccountService accountService) {
    // todo... 业务逻辑
    accountService.remove(this);
  }

  public void save(AccountService accountService) {
    // handle
    if (this.id == null) {
      // 新增逻辑
      // 设置初始状态
      this.state = AccountState.of(AccountStateEnum.INIT);
    } else {
      // 更新逻辑
      if (!accountService.allowModify(this)) {
        throw new BusinessException("不允许修改");
      }
    }
    // save
    accountService.save(this);
  }

  public void invalidate() {
    // 领域都是合法的，可以忽略空指针问题。
    if (AccountStateEnum.ACTIVE.equals(this.state.getValue())) {
      this.state = AccountState.of(AccountStateEnum.TERMINATED);
    } else {
      throw new BusinessException("当前状态无法失效。");
    }
  }
}
