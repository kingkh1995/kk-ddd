package com.kkk.op.user.repository.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.converter.UserDataConverter;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.persistence.mapper.UserMapper;
import com.kkk.op.user.repository.UserRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author KaiKoo
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;
    private final UserDataConverter userDataConverter;

    public UserRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
        userDataConverter = UserDataConverter.getInstance();
    }

    @Override
    public void attach(User aggregate) {

    }

    @Override
    public void detach(User aggregate) {

    }

    @Override
    public User find(LongId longId) {
        return userDataConverter.fromData(userMapper.selectById(longId.getValue()));
    }

    @Override
    public void remove(User aggregate) {
        userMapper.delete(Wrappers.query(userDataConverter.toData(aggregate)));
    }

    @Override
    public void save(User aggregate) {
        if (aggregate.getId() != null) {
            userMapper.updateById(userDataConverter.toData(aggregate));
        } else {
            userMapper.insert(userDataConverter.toData(aggregate));
        }
    }
}
