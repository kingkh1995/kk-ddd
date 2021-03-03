package com.kkk.op.user.repository.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kkk.op.support.changeTracking.AggregateRepositorySupport;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.converter.AccountDataConverter;
import com.kkk.op.user.converter.UserDataConverter;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.persistence.mapper.UserMapper;
import com.kkk.op.user.repository.UserRepository;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author KaiKoo
 */
@Repository
public class UserRepositoryImpl extends AggregateRepositorySupport<User, LongId> implements
        UserRepository {

    private final UserMapper userMapper;
    private final UserDataConverter userDataConverter;
    private final AccountDataConverter accountDataConverter;


    public UserRepositoryImpl(UserMapper userMapper) {
        super(User.class);
        this.userMapper = userMapper;
        userDataConverter = UserDataConverter.getInstance();
        accountDataConverter = AccountDataConverter.getInstance();
    }

    @Override
    public void attach(User aggregate) {

    }

    @Override
    public void detach(User aggregate) {

    }

    @Override
    public User find(@NotNull LongId longId) {
        User user = userDataConverter.fromData(userMapper.selectById(longId.getValue()));
        //todo... find account
        return user;
    }

    @Transactional
    @Override
    public void remove(@NotNull User aggregate) {
        userMapper.delete(Wrappers.query(userDataConverter.toData(aggregate)));
        //todo... remove account
    }

    @Transactional
    @Override
    public LongId save(@NotNull User aggregate) {
        return null;
    }

}
