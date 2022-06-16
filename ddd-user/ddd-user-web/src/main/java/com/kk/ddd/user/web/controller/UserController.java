package com.kk.ddd.user.web.controller;

import com.kk.ddd.support.annotation.BaseController;
import com.kk.ddd.support.model.command.UserModifyCommand;
import com.kk.ddd.support.model.dto.UserDTO;
import com.kk.ddd.support.model.provider.UserProvider;
import com.kk.ddd.user.application.service.UserAppService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * todo...
 *
 * @author KaiKoo
 */
@Slf4j
@RequiredArgsConstructor
@BaseController
public class UserController implements UserProvider {

  private final UserAppService userAppService;

  @Override
  public Long createUser(UserModifyCommand createCommand) {
    return 0L;
  }

  @Override
  public Long updateUser(Long userId, UserModifyCommand updateCommand) {
    return 0L;
  }

  @Override
  public UserDTO queryById(Long userId) {
    return userAppService.queryUser(userId);
  }

  @Override
  public List<UserDTO> queryByIds(Set<Long> userIds) {
    return userAppService.queryUsers(userIds);
  }
}
