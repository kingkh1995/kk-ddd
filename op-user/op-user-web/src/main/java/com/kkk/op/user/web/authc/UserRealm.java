package com.kkk.op.user.web.authc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 登录认证域
 *
 * @see org.apache.shiro.realm.CachingRealm 默认继承了该类，可以添加缓存功能。
 * @see org.apache.shiro.realm.AuthorizingRealm 如需要实现权限功能继承该类。
 * @author KaiKoo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRealm extends AuthenticatingRealm implements InitializingBean {

  private final AuthcService service;

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
      throws AuthenticationException {
    return service.getAuthenticationInfo((UsernamePasswordToken) token, getName());
  }

  /**
   * @see org.apache.shiro.crypto.hash.Hash 支持的加密算法种类
   * @see org.apache.shiro.authc.credential.PasswordMatcher 交由第三方服务验证匹配
   */
  @Override
  public void afterPropertiesSet() {
    // 设置域名
    super.setName("UserRealm");
    // 设置token类型，调用之前会先调用supports方法判断通过，才会调用doGetAuthenticationInfo方法
    super.setAuthenticationTokenClass(UsernamePasswordToken.class);
    // 设置加密匹配器
    var hashedCredentialsMatcher = new HashedCredentialsMatcher(AuthcService.HASH_ALGORITHM_NAME);
    hashedCredentialsMatcher.setHashIterations(AuthcService.HASH_INTERATIONS);
    hashedCredentialsMatcher.setStoredCredentialsHexEncoded(AuthcService.HEX_ENCODED_STORED);
    super.setCredentialsMatcher(hashedCredentialsMatcher);
  }
}
