package com.kkk.op.user.domain.entity;

import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.types.LongId;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户 <br>
 * todo...
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
@ToString
@Getter
@Builder
public class User extends Aggregate<LongId> {

  @Setter(AccessLevel.PROTECTED)
  private LongId id;

  private final String name;

  private final String username;

  private final String password;

  private final String gender;

  private final Byte age;

  private final String email;

  /** 用户账号 */
  @Setter private List<Account> accounts;

  @Override
  public User snapshot() {
    var builder = builder();
    builder.id(this.id).name(this.name);
    return builder.build();
  }

  @Override
  public void validate() {}
}
