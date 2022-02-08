package com.kkk.op.user.query.service.impl;

import com.kkk.op.support.annotation.QueryService;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.query.service.UserQueryService;
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
@QueryService // 标记为query service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

  private final UserRepository userRepository;

  @Override
  public Optional<User> findByUsername(String username) {
    return userRepository.find(username);
  }
}
