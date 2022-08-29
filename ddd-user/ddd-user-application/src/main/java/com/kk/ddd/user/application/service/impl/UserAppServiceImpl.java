package com.kk.ddd.user.application.service.impl;

import com.kk.ddd.support.model.dto.UserAuthInfo;
import com.kk.ddd.support.model.dto.UserDTO;
import com.kk.ddd.user.application.assembler.UserDTOAssembler;
import com.kk.ddd.user.application.service.UserAppService;
import com.kk.ddd.user.domain.service.UserService;
import com.kk.ddd.user.domain.type.UserId;
import com.kk.ddd.user.query.service.UserQueryService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
  public Optional<UserAuthInfo> getAuthcInfo(String username) {
    return userQueryService.findByUsername(username).map(userDTOAssembler::toAuthcInfo);
  }

  @Override
  @Transactional
  public void changePassword(UserAuthInfo authcInfo) {
    // 使用QueryService查询领域对象
    var user = userQueryService.find(UserId.valueOf(authcInfo.getId(), "id")).get();
    // 执行修改密码操作
    user.changePassword(authcInfo.getEncryptedPassword());
    // 执行save操作
    user.save(userService);
    // 发送事件
  }

  @Override
  public UserDTO queryUser(Long userId) {
    return userQueryService.find(UserId.valueOf(userId, "id")).map(userDTOAssembler::toDTO).get();
  }

  @Override
  public List<UserDTO> queryUsers(Set<Long> userIds) {
    return userDTOAssembler.toDTO(
        userQueryService.find(
            userIds.stream().map(l -> UserId.valueOf(l, "id")).collect(Collectors.toSet())));
  }
}
