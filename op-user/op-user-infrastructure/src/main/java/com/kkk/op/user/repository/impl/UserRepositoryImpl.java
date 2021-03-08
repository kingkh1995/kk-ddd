package com.kkk.op.user.repository.impl;

import com.kkk.op.support.changeTracking.AggregateRepositorySupport;
import com.kkk.op.support.changeTracking.ThreadLocalAggregateTrackingManager;
import com.kkk.op.support.changeTracking.diff.EntityDiff;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.converter.AccountDataConverter;
import com.kkk.op.user.converter.UserDataConverter;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.persistence.mapper.UserMapper;
import com.kkk.op.user.repository.UserRepository;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;

/**
 * Aggregate类Repository实现类
 * @author KaiKoo
 */
@Repository
public class UserRepositoryImpl extends AggregateRepositorySupport<User, LongId> implements
        UserRepository {

    private final UserMapper userMapper;
    private final UserDataConverter userDataConverter;
    private final AccountDataConverter accountDataConverter;


    public UserRepositoryImpl(UserMapper userMapper) {
        // 使用ThreadLocalAggregateTrackingManager
        super(new ThreadLocalAggregateTrackingManager());
        this.userMapper = userMapper;
        userDataConverter = UserDataConverter.getInstance();
        accountDataConverter = AccountDataConverter.getInstance();
    }

    @Override
    protected LongId onInsert(@NotNull User aggregate) {
        return null;
    }

    @Override
    protected User onSelect(@NotNull LongId longId) {
        return null;
    }

    @Override
    protected void onUpdate(@NotNull User aggregate, @NotNull EntityDiff diff) {

    }

    @Override
    protected void onDelete(@NotNull User aggregate) {

    }
}
