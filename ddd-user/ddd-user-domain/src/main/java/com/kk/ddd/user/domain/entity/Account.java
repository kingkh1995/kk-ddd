package com.kk.ddd.user.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kk.ddd.support.core.Entity;
import com.kk.ddd.support.diff.DiffIgnore;
import com.kk.ddd.support.exception.BusinessException;
import com.kk.ddd.support.type.Version;
import com.kk.ddd.support.util.NameGenerator;
import com.kk.ddd.user.domain.service.AccountService;
import com.kk.ddd.user.domain.type.AccountId;
import com.kk.ddd.user.domain.type.AccountType;
import com.kk.ddd.user.domain.type.UserId;
import java.time.Instant;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户绑定账户
 *
 * @author KaiKoo
 */
@JsonDeserialize(builder = Account.AccountBuilder.class) // 设置反序列化使用Builder
@Builder
@EqualsAndHashCode(callSuper = true)
@Getter
public class Account extends Entity<AccountId> {

  @Setter(AccessLevel.PROTECTED)
  private AccountId id;

  private UserId userId;

  private AccountType type;

  private String principal;

  private Instant unbindTime;

  @DiffIgnore private Instant createTime;

  @DiffIgnore private Instant updateTime;

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
    } else {
      // 更新逻辑
      if (!accountService.allowModify(this)) {
        throw new BusinessException("不允许修改");
      }
    }
    // save
    accountService.save(this);
  }

  public Account unbind() {
    if (Objects.nonNull(this.unbindTime)) {
      throw new BusinessException("已经解绑！");
    }
    this.unbindTime = Instant.now();
    return this;
  }

  public Account validate() {
    // todo... 校验逻辑
    return this;
  }
}
