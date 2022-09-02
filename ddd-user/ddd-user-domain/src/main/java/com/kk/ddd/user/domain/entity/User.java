package com.kk.ddd.user.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kk.ddd.support.core.Aggregate;
import com.kk.ddd.support.diff.DiffIgnore;
import com.kk.ddd.support.enums.UserStateEnum;
import com.kk.ddd.support.exception.BusinessException;
import com.kk.ddd.support.type.Version;
import com.kk.ddd.support.util.NameGenerator;
import com.kk.ddd.user.domain.service.UserService;
import com.kk.ddd.user.domain.type.Hash;
import com.kk.ddd.user.domain.type.UserId;
import com.kk.ddd.user.domain.type.UserState;
import com.kk.ddd.user.domain.type.Username;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户 <br>
 * todo... 属性替换位DP
 *
 * @author KaiKoo
 */
@JsonDeserialize(builder = User.UserBuilder.class)
@Builder
@Getter
@EqualsAndHashCode(callSuper = true)
public final class User extends Aggregate<UserId> {

  // from user
  @Setter(AccessLevel.PROTECTED)
  private UserId id;
  private UserState state;
  private Username name;
  private String phone;
  private String email;
  @DiffIgnore private Instant createTime; // Entity中使用Instant对应PO中的Date类型
  @DiffIgnore private Instant updateTime;
  @DiffIgnore private Version version;

  // from user_base

  /**
   * 性别
   */
  private String gender;

  /**
   * 生日
   */
  private LocalDate birthday;

  /**
   * 头像
   */
  private String profile;

  // from user_security

  /**
   * 加密后密码
   */
  private String encryptedPassword;

  /**
   * 哈希算法
   */
  private Hash hash;

  /** 用户绑定账户，from account。 */
  private List<Account> accounts;

  @Override
  public String generateLockName(NameGenerator lockNameGenerator) {
    return lockNameGenerator.generate(
        "user", "User", Objects.requireNonNull(this.getId()).identifier());
  }

  public void save(UserService userService) {
    // todo...
    // handle
    if (Objects.isNull(this.getId())) {
      // 新增逻辑
      // 设置初始状态
      this.state = UserState.of(UserStateEnum.INIT);
    } else {
      // 更新逻辑
    }
    // save
    userService.save(this);
  }

  public void invalidate() {
    // 领域都是合法的，可以忽略空指针问题。
    if (UserStateEnum.ACTIVE.equals(this.state.toEnum())) {
      this.state = UserState.of(UserStateEnum.TERMINATED);
    } else {
      throw new BusinessException("当前状态无法失效！");
    }
  }

  public void changePassword(String encryptedPassword) {
    if (encryptedPassword == null) {
      return;
    }
    // todo... 逻辑校验
    // 更新属性
//    this.password = encryptedPassword;
  }
}
