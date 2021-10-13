package com.kkk.op.user.web.controller;

import com.kkk.op.support.annotation.BaseController;
import com.kkk.op.support.model.command.UserModifyCommand;
import com.kkk.op.user.web.provider.UserProvider;
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

  @Override
  public long createUser(UserModifyCommand createCommand) {
    return 0;
  }

  @Override
  public long updateUser(Long userId, UserModifyCommand updateCommand) {
    return 0;
  }
}
