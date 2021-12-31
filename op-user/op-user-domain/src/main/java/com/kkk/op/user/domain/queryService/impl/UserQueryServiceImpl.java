package com.kkk.op.user.domain.queryService.impl;

import com.kkk.op.support.annotation.QueryService;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.domain.queryService.UserQueryService;
import com.kkk.op.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Slf4j
@QueryService // 标记为queryservice
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

  private final UserRepository userRepository;

  @Override
  public Optional<User> findByUsername(String username) {
    return userRepository.find(username);
  }
}
