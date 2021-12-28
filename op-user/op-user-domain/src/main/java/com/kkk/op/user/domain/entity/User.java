package com.kkk.op.user.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.marker.NameGenerator;
import com.kkk.op.user.domain.service.UserService;
import com.kkk.op.user.domain.types.UserId;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户 <br>
 * todo...
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
  public void validate() {}

  @Override
  public String generateLockName(NameGenerator lockNameGenerator) {
    return lockNameGenerator.generate(
        "user", "User", Objects.requireNonNull(this.getId()).identifier());
  }

  public void savePassword(UserService userService) {
    // todo...
    var old = userService.find(this.id).get();
    old.password = this.password;
    userService.save(old);
  }
}
