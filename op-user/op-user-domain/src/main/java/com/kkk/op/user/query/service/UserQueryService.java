package com.kkk.op.user.query.service;

import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.domain.type.UserId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * todo... 读写分离，QueryService <br>
 *
 * @author KaiKoo
 */
public interface UserQueryService {

  Optional<User> find(@NotNull UserId userId);

  List<User> find(@NotEmpty Set<UserId> userIds);

  Optional<User> findByUsername(String username);
}
