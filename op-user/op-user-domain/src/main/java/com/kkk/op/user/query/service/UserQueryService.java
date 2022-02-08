package com.kkk.op.user.query.service;

import com.kkk.op.user.domain.entity.User;
import java.util.Optional;

/**
 * todo... 读写分离，QueryService <br>
 *
 * @author KaiKoo
 */
public interface UserQueryService {

  Optional<User> findByUsername(String username);
}
