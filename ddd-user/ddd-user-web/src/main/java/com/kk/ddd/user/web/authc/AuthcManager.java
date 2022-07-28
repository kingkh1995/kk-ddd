package com.kk.ddd.user.web.authc;

import com.kk.ddd.support.model.command.AuthcCommand;
import com.kk.ddd.support.model.dto.UserAuthcInfo;
import com.kk.ddd.support.model.group.Inner;
import com.kk.ddd.support.model.query.AuthcQuery;
import com.kk.ddd.user.application.service.UserAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 核心认证服务 <br>
 * 加密方式：md5算法、加盐（用户ID）计算3次、使用Base64编码
 *
 * @author KaiKoo
 */
@Slf4j
@Validated
@Component
@RequiredArgsConstructor
public class AuthcManager {

  // MD5为哈希算法，即信息摘要，会产生一个固定128位的散列值。
  public static final String HASH_ALGORITHM_NAME = Md5Hash.ALGORITHM_NAME;

  public static final int HASH_ITERATIONS = 3;

  public static final boolean HEX_ENCODED_STORED = false;

  private final UserAppService service;

  /**
   * 获取认证信息
   *
   * @param query
   * @return
   */
  public UserAuthenticationInfo getAuthenticationInfo(@Validated AuthcQuery query) {
    return service
        .getAuthcInfo(query.getUsername())
        .map(
            authcInfo ->
                new UserAuthenticationInfo(
                    query.getRealmName(),
                    authcInfo.getEncryptedPassword(),
                    getSalt(authcInfo),
                    query.getRealmName(),
                    authcInfo))
        .orElse(null);
  }

  /**
   * 账号登录
   *
   * @param command
   */
  public void login(@Validated(Inner.class) AuthcCommand command) {
    SecurityUtils.getSubject()
        .login(new UsernamePasswordToken(command.getUsername(), command.getPlaintextPassword()));
  }

  /**
   * 修改账号密码 <br>
   * todo... 校验原密码
   *
   * @param command
   */
  public void changePassword(@Validated(Inner.class) AuthcCommand command) {
    var authcInfo = service.getAuthcInfo(command.getUsername()).get();
    var newPassword = encryptPassword(command.getPlaintextPassword(), getSalt(authcInfo));
    // 做到幂等，与原密码相同直接return
    if (newPassword.equals(authcInfo.getEncryptedPassword())) {
      log.info("New password is equal to the old, return!");
      return;
    }
    authcInfo.setEncryptedPassword(newPassword);
    service.changePassword(authcInfo);
  }

  private String encryptPassword(String plaintextPassword, ByteSource salt) {
    var byteSource = new SimpleHash(HASH_ALGORITHM_NAME, plaintextPassword, salt, HASH_ITERATIONS);
    if (HEX_ENCODED_STORED) {
      return byteSource.toHex();
    } else {
      return byteSource.toBase64();
    }
  }

  private ByteSource getSalt(UserAuthcInfo authcInfo) {
    return new SimpleByteSource(String.valueOf(authcInfo.getId()));
  }
}
