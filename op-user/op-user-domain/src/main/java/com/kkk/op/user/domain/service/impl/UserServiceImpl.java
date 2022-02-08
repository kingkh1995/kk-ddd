package com.kkk.op.user.domain.service.impl;

import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.domain.service.UserService;
import com.kkk.op.user.domain.type.UserId;
import com.kkk.op.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public void save(@NotNull User user) {
    userRepository.save(user);
  }

  @Override
  public void remove(@NotNull User user) {
    userRepository.remove(user);
  }

  @Override
  public Optional<User> find(@NotNull UserId userId) {
    return userRepository.find(userId);
  }

  @Override
  public List<User> find(@NotEmpty Set<UserId> userIds) {
    return userRepository.find(userIds);
  }
}
