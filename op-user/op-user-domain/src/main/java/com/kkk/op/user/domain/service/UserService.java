package com.kkk.op.user.domain.service;

import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.User;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 *
 * @author KaiKoo
 */
public interface UserService {

    /**
     * 通过 ID 查找
     */
    User find(@NotNull LongId id);

    /**
     * 通过 IDs 查找
     */
    List<User> list(@NotEmpty List<LongId> ids);

    /**
     * 移除
     */
    void remove(@NotNull User entity);

    /**
     * 保存
     */
    void save(@NotNull User entity);

}
