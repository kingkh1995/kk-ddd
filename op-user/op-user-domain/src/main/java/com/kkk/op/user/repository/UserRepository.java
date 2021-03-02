package com.kkk.op.user.repository;

import com.kkk.op.support.interfaces.AggregateRepository;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.User;

/**
 *
 * @author KaiKoo
 */
public interface UserRepository extends AggregateRepository<User, LongId> {

}
