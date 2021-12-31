package com.kkk.op.user.web.controller;

import com.kkk.op.support.annotation.BaseController;
import com.kkk.op.support.model.command.UserModifyCommand;
import com.kkk.op.support.model.dto.UserDTO;
import com.kkk.op.user.application.service.UserAppService;
import com.kkk.op.user.web.provider.UserProvider;
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
  public long createUser(UserModifyCommand createCommand) {
    return 0;
  }

  @Override
  public long updateUser(Long userId, UserModifyCommand updateCommand) {
    return 0;
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
