package com.kk.ddd.user.application.service;

import com.kk.ddd.support.model.dto.UserAuthInfo;
import com.kk.ddd.support.model.dto.UserDTO;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * <br>
 *
 * @author KaiKoo
 */
public interface UserAppService {

  /**
   * 根据用户名获取认证信息
   *
   * @param username
   * @return
   */
  Optional<UserAuthInfo> getAuthInfo(String username);

  /**
   * 修改密码
   *
   * @param authInfo
   */
  void changePassword(UserAuthInfo authInfo);

  UserDTO queryUser(Long userId);

  List<UserDTO> queryUsers(Set<Long> userIds);
}
