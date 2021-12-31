package com.kkk.op.user.repository;

import com.kkk.op.support.marker.AggregateRepository;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.domain.types.UserId;
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
