package com.kkk.op.user.application.service.impl;

import com.kkk.op.support.model.dto.UserAuthcInfo;
import com.kkk.op.user.application.service.UserAppService;
import com.kkk.op.user.assembler.UserDTOAssembler;
import com.kkk.op.user.domain.queryService.UserQueryService;
import com.kkk.op.user.domain.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * todo... <br>
 *
 * @author KaiKoo
 */
@Service
@RequiredArgsConstructor
public class UserAppServiceImpl implements UserAppService {

  private final UserService userService;

  private final UserQueryService userQueryService;

  private final UserDTOAssembler userDTOAssembler;

  @Override
  public Optional<UserAuthcInfo> getAuthcInfo(String username) {
    return userQueryService.findByUsername(username).map(userDTOAssembler::toAuthcInfo);
  }

  @Override
  @Transactional
  public void savePassword(UserAuthcInfo authcInfo) {
    userDTOAssembler.fromAuthcInfo(authcInfo).savePassword(userService);
  }
}
