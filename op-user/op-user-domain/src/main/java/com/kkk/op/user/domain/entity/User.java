package com.kkk.op.user.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.types.LongId;
import java.util.List;
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
@EqualsAndHashCode
@Getter
@Builder
public class User extends Aggregate<LongId> {

  @Setter(AccessLevel.PROTECTED)
  private LongId id;

  private String name;

  private String username;

  private String password;

  private String gender;

  private Byte age;

  private String email;

  /** 用户账号 */
  @Setter // todo... 优化
  private List<Account> accounts;

  @Override
  public void validate() {}
}
