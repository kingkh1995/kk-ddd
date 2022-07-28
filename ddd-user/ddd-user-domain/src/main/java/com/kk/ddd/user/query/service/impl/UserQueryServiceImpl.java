package com.kk.ddd.user.query.service.impl;

import com.kk.ddd.user.domain.entity.User;
import com.kk.ddd.user.domain.type.UserId;
import com.kk.ddd.user.query.service.UserQueryService;
import com.kk.ddd.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

  private final UserRepository userRepository;

  @Override
  public Optional<User> find(@NotNull UserId userId) {
    return userRepository.find(userId);
  }

  @Override
  public List<User> find(@NotEmpty Set<UserId> userIds) {
    return userRepository.find(userIds);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return userRepository.find(username);
  }
}
