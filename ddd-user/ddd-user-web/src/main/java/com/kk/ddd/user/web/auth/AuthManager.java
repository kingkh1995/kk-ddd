package com.kk.ddd.user.web.auth;

import com.kk.ddd.support.model.command.AuthCommand;
import com.kk.ddd.support.model.command.PasswordModifyCommand;
import com.kk.ddd.support.model.dto.UserAuthInfo;
import com.kk.ddd.support.model.group.Inner;
import com.kk.ddd.support.model.query.AuthQuery;
import com.kk.ddd.support.shiro.SimpleAuthenticationToken;
import com.kk.ddd.user.application.service.UserAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 核心认证服务 <br>
 * 属于基础设施service，故可直接使用基础设施层mapper。
 *
 * @author KaiKoo
 */
@Slf4j
@Validated
@Component
@RequiredArgsConstructor
public class AuthManager {

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
  public UserAuthenticationInfo getAuthenticationInfo(@Validated AuthQuery query) {
    return service
        .getAuthInfo(query.getUsername())
        .map(
            authInfo ->
                new UserAuthenticationInfo(
                    query.getRealmName(),
                    authInfo.getEncryptedPassword(),
                    getSalt(authInfo),
                    query.getRealmName(),
                    authInfo))
        .orElse(null);
  }

  /**
   * 账号登录
   *
   * @param command
   */
  public void login(@Validated AuthCommand command) {
    // todo... 针对不同的auth_type创建不同类型的token
    SecurityUtils.getSubject()
        .login(new SimpleAuthenticationToken(command.getPrincipal(), command.getCredential()));
  }

  /**
   * 修改账号密码 <br>
   * todo... 校验原密码
   *
   * @param command
   */
  public void changePassword(@Validated(Inner.class) PasswordModifyCommand command) {
    var authInfo = service.getAuthInfo(command.getUsername()).get();
    var newPassword = encryptPassword(command.getPlaintextPassword(), getSalt(authInfo));
    // 做到幂等，与原密码相同直接return
    if (newPassword.equals(authInfo.getEncryptedPassword())) {
      log.info("New password is equal to the old, return!");
      return;
    }
    authInfo.setEncryptedPassword(newPassword);
    service.changePassword(authInfo);
  }

  private String encryptPassword(String plaintextPassword, ByteSource salt) {
    var byteSource = new SimpleHash(HASH_ALGORITHM_NAME, plaintextPassword, salt, HASH_ITERATIONS);
    if (HEX_ENCODED_STORED) {
      return byteSource.toHex();
    } else {
      return byteSource.toBase64();
    }
  }

  private ByteSource getSalt(UserAuthInfo authInfo) {
    return new SimpleByteSource(String.valueOf(authInfo.getId()));
  }
}
