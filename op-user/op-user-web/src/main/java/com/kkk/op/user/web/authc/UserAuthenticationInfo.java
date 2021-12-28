package com.kkk.op.user.web.authc;

import com.kkk.op.support.model.dto.UserAuthcInfo;
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

  @Getter private UserAuthcInfo userAuthcInfo;

  public UserAuthenticationInfo(
      Object principal,
      Object hashedCredentials,
      ByteSource credentialsSalt,
      String realmName,
      UserAuthcInfo userAuthcInfo) {
    super(principal, hashedCredentials, credentialsSalt, realmName);
    this.userAuthcInfo = userAuthcInfo;
  }
}
