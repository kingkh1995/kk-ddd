package com.kk.ddd.user.web.authc;

import com.kk.ddd.support.model.dto.UserAuthInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.util.ByteSource;

/**
 * <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true)
public class UserAuthenticationInfo extends SimpleAuthenticationInfo {

  @Getter private final UserAuthInfo userAuthInfo;

  public UserAuthenticationInfo(
      Object principal,
      Object hashedCredentials,
      ByteSource credentialsSalt,
      String realmName,
      UserAuthInfo userAuthInfo) {
    super(principal, hashedCredentials, credentialsSalt, realmName);
    this.userAuthInfo = userAuthInfo;
  }
}
