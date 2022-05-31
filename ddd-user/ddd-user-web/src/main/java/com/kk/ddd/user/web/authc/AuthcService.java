package com.kk.ddd.user.web.authc;

import com.kk.ddd.support.model.command.AuthcCommand;
import com.kk.ddd.support.model.dto.UserAuthcInfo;
import com.kk.ddd.support.util.IllegalArgumentExceptions;
import com.kk.ddd.user.application.service.UserAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;
import org.springframework.stereotype.Component;

/**
 * 核心认证服务 <br>
 * 加密方式：md5算法、加盐（用户ID）计算3次、使用Base64编码
 *
 * @author KaiKoo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthcService {

  public static final String HASH_ALGORITHM_NAME = Md5Hash.ALGORITHM_NAME;

  public static final int HASH_INTERATIONS = 3;

  public static final boolean HEX_ENCODED_STORED = false;

  private final UserAppService service;

  public UserAuthenticationInfo getAuthenticationInfo(
      UsernamePasswordToken token, String realmName) {
    return service
        .getAuthcInfo(token.getUsername())
        .map(
            authcInfo ->
                new UserAuthenticationInfo(
                    token.getUsername(),
                    authcInfo.getEncryptedPassword(),
                    getSalt(authcInfo),
                    realmName,
                    authcInfo))
        .orElse(null);
  }

  public void changePassword(AuthcCommand command) {
    var plaintextPassword = command.getPlaintextPassword();
    if (plaintextPassword == null || plaintextPassword.isBlank()) {
      throw IllegalArgumentExceptions.forIsNull("[plaintext password]");
    }
    var authcInfo = service.getAuthcInfo(command.getUsername()).get();
    var newPassword = encryptPassword(plaintextPassword, getSalt(authcInfo));
    // 做到幂等，与原密码相同直接return
    if (newPassword.equals(authcInfo.getEncryptedPassword())) {
      log.info("New password is equal to the old, return!");
      return;
    }
    authcInfo.setEncryptedPassword(newPassword);
    service.changePassword(authcInfo);
  }

  private String encryptPassword(String plaintextPassword, ByteSource salt) {
    var byteSource = new SimpleHash(HASH_ALGORITHM_NAME, plaintextPassword, salt, HASH_INTERATIONS);
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
