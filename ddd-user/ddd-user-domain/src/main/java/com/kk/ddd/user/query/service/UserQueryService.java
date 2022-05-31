package com.kk.ddd.user.query.service;

import com.kk.ddd.support.marker.QueryService;
import com.kk.ddd.user.domain.entity.User;
import com.kk.ddd.user.domain.type.UserId;
import java.util.Optional;

/**
 * todo... 读写分离，QueryService <br>
 *
 * @author KaiKoo
 */
public interface UserQueryService extends QueryService<User, UserId> {

  Optional<User> findByUsername(String username);
}
