package com.kkk.op.user.query.service;

import com.kkk.op.support.marker.QueryService;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.domain.type.UserId;
import java.util.Optional;

/**
 * todo... 读写分离，QueryService <br>
 *
 * @author KaiKoo
 */
public interface UserQueryService extends QueryService<User, UserId> {

  Optional<User> findByUsername(String username);
}
