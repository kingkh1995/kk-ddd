package com.kkk.op.user.application.service;

import com.kkk.op.support.model.dto.UserAuthcInfo;
import com.kkk.op.support.model.dto.UserDTO;
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
  Optional<UserAuthcInfo> getAuthcInfo(String username);

  /**
   * 保存密码
   *
   * @param authcInfo
   */
  void savePassword(UserAuthcInfo authcInfo);

  UserDTO queryUser(Long userId);

  List<UserDTO> queryUsers(Set<Long> userIds);
}
