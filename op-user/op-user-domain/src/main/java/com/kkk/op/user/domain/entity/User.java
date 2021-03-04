package com.kkk.op.user.domain.entity;

import com.kkk.op.support.markers.Aggregate;
import com.kkk.op.support.types.LongId;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户
 * @author KaiKoo
 */
@Data
@Accessors(chain = true)
public class User implements Aggregate<LongId> {

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
    private List<Account> account;

    @Override
    public User snapshot() {
        User snapshot = new User();
        snapshot.setId(this.id)
                .setName(this.name);
        return snapshot;
    }
}
