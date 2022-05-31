package com.kk.ddd.user.domain.service.impl;

import com.kk.ddd.user.domain.entity.User;
import com.kk.ddd.user.domain.service.UserService;
import com.kk.ddd.user.repository.UserRepository;
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
}
