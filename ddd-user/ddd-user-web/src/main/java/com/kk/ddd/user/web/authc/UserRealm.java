package com.kk.ddd.user.web.authc;

import com.kk.ddd.support.base.LocalRequestContextHolder;
import com.kk.ddd.support.model.dto.UserAuthcInfo;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
public class UserRealm extends AuthenticatingRealm implements SmartInitializingSingleton {

  private AuthcService authcService;

  @Autowired // 使用setter注入，优点是可以被继承重写，灵活性高，缺点是属性无法定义为final。
  @Lazy // Shiro相关bean会被提前加载，所以依赖的其他bean要设置为延后加载，否则BeanPostProcessorChecker会提示信息。
  public void setAuthcService(AuthcService authcService) {
    this.authcService = authcService;
  }

  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
      throws AuthenticationException {
    var userAuthenticationInfo =
        authcService.getAuthenticationInfo((UsernamePasswordToken) token, getName());
    Optional.ofNullable(userAuthenticationInfo).orElseThrow(UnknownAccountException::new);
    save2LocalRequestContext(userAuthenticationInfo.getUserAuthcInfo());
    return userAuthenticationInfo;
  }

  private void save2LocalRequestContext(UserAuthcInfo userAuthcInfo) {
    var requestContext = LocalRequestContextHolder.get();
    requestContext.setOperatorId(userAuthcInfo.getId());
    requestContext.setClaims(
        Map.of("username", userAuthcInfo.getUsername(), "name", userAuthcInfo.getName()));
    log.info("requestContext => {}", requestContext);
  }

  /**
   * @see org.apache.shiro.crypto.hash.Hash 支持的加密算法种类
   * @see org.apache.shiro.authc.credential.PasswordMatcher 交由第三方服务验证匹配
   */
  @Override
  public void afterSingletonsInstantiated() {
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
