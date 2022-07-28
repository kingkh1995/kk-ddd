package com.kk.ddd.user.repository;

import com.kk.ddd.support.core.AggregateRepository;
import com.kk.ddd.user.domain.entity.User;
import com.kk.ddd.user.domain.type.UserId;
import java.util.Optional;

/**
 * <br>
 *
 * @author KaiKoo
 */
public interface UserRepository extends AggregateRepository<User, UserId> {

  // todo... 参数替换成DP。
  Optional<User> find(String username);
}
