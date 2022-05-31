package com.kk.ddd.user.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kk.ddd.support.base.Aggregate;
import com.kk.ddd.support.util.NameGenerator;
import com.kk.ddd.user.domain.service.UserService;
import com.kk.ddd.user.domain.type.UserId;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户 <br>
 * todo... 属性使用DP
 *
 * @author KaiKoo
 */
@JsonDeserialize(builder = User.UserBuilder.class)
@EqualsAndHashCode(callSuper = true)
@Getter
@Builder
public class User extends Aggregate<UserId> {

  @Setter(AccessLevel.PROTECTED)
  private UserId id;

  private String name;

  private String username;

  private String password;

  private String gender;

  private Byte age;

  private String email;

  /** 用户账户 */
  private List<Account> accounts;

  @Override
  public String generateLockName(NameGenerator lockNameGenerator) {
    return lockNameGenerator.generate(
        "user", "User", Objects.requireNonNull(this.getId()).identifier());
  }

  public void save(UserService userService) {
    // todo...
    // handle
    if (this.id == null) {
      // 新增逻辑
    } else {
      // 更新逻辑
    }
    // save
    userService.save(this);
  }

  public void changePassword(String encryptedPassword) {
    if (encryptedPassword == null) {
      return;
    }
    // todo... 逻辑校验
    // 更新属性
    this.password = encryptedPassword;
  }
}
