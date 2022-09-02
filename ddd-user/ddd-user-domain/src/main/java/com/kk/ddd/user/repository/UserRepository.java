package com.kk.ddd.user.repository;

import com.kk.ddd.support.core.AggregateRepository;
import com.kk.ddd.user.domain.entity.User;
import com.kk.ddd.user.domain.type.UserId;
import com.kk.ddd.user.domain.type.Username;
import java.util.Optional;
import javax.validation.constraints.NotNull;

/**
 * <br>
 *
 * @author KaiKoo
 */
public interface UserRepository extends AggregateRepository<User, UserId> {

  Optional<User> find(@NotNull Username name);
}
