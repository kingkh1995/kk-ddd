package com.kkk.op.user.domain.entity;

import com.kkk.op.support.marker.Aggregate;
import com.kkk.op.support.types.LongId;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户
 * @author KaiKoo
 */
@EqualsAndHashCode
@ToString
@Getter
@Builder
public class User implements Aggregate<LongId> {

    @Setter //todo... 删除
    private LongId id;

    private String name;

    private String username;

    private String password;

    private String gender;

    private Byte age;

    private String email;

    /**
     * 用户账号
     */
    @Setter
    private List<Account> accounts;

    @Override
    public User snapshot() {
        UserBuilder builder = User.builder();
        builder.id(this.id)
                .name(this.name);
        return builder.build();
    }
}
