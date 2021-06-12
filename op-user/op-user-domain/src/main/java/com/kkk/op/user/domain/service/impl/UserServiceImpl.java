package com.kkk.op.user.domain.service.impl;

import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.domain.service.UserService;
import com.kkk.op.user.repository.UserRepository;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <br>
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
  public User find(@NotNull LongId longId) {
    return userRepository.find(longId);
  }

  @Override
  public void remove(@NotNull User user) {
    userRepository.remove(user);
  }

  @Override
  public void save(@NotNull User user) {
    userRepository.save(user);
  }

  @Override
  public List<User> list(@NotEmpty Set<LongId> longIds) {
    return userRepository.list(longIds);
  }
}
