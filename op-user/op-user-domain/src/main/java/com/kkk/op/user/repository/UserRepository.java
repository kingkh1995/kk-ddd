package com.kkk.op.user.repository;

import com.kkk.op.support.marker.AggregateRepository;
import com.kkk.op.support.type.LongId;
import com.kkk.op.user.domain.entity.User;

/**
 * <br>
 *
 * @author KaiKoo
 */
public interface UserRepository extends AggregateRepository<User, LongId> {}
