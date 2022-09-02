package com.kk.ddd.user.application.service.impl;

import com.kk.ddd.support.model.dto.UserAuthInfo;
import com.kk.ddd.support.model.dto.UserDTO;
import com.kk.ddd.user.application.assembler.UserDTOAssembler;
import com.kk.ddd.user.application.service.UserAppService;
import com.kk.ddd.user.domain.type.UserId;
import com.kk.ddd.user.domain.type.Username;
import com.kk.ddd.user.repository.UserRepository;
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

  private final UserRepository userRepository;

  private final UserDTOAssembler userDTOAssembler;

  @Override
  public Optional<UserAuthInfo> getAuthInfo(String username) {
    return userRepository
            .find(Username.valueOf(username, "用户名"))
            .map(userDTOAssembler::toAuthInfo);
  }

  @Override
  @Transactional
  public void changePassword(UserAuthInfo authInfo) {
    // 使用QueryService查询领域对象
    var user = userRepository.find(UserId.valueOf(authInfo.getId(), "id")).get();
    // 执行修改密码操作
    user.changePassword(authInfo.getEncryptedPassword());
    // 持久化
    userRepository.save(user);
    // 发送事件
  }

  @Override
  public UserDTO queryUser(Long userId) {
    return userRepository.find(UserId.valueOf(userId, "id")).map(userDTOAssembler::toDTO).get();
  }

  @Override
  public List<UserDTO> queryUsers(Set<Long> userIds) {
    return userDTOAssembler.toDTO(
            userRepository.find(
                    userIds.stream().map(l -> UserId.valueOf(l, "id"))
                            .collect(Collectors.toSet())));
  }
}
