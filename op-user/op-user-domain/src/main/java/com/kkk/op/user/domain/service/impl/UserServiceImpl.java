package com.kkk.op.user.domain.service.impl;

import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.domain.service.UserService;
import com.kkk.op.user.repository.UserRepository;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author KaiKoo
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User find(@NotNull LongId id) {
        return userRepository.find(id);
    }

    @Override
    public List<User> list(@NotEmpty List<LongId> ids) {
        return userRepository.list(ids);
    }

    @Override
    public void remove(@NotNull User entity) {
        userRepository.remove(entity);
    }

    @Override
    public void save(@NotNull User entity) {
        userRepository.save(entity);
    }
}
