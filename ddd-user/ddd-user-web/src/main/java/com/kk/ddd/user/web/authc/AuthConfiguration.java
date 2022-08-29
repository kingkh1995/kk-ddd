package com.kk.ddd.user.web.authc;

import com.auth0.jwt.algorithms.Algorithm;
import com.kk.ddd.support.annotation.LiteConfiguration;
import com.kk.ddd.support.bean.Result;
import com.kk.ddd.support.shiro.JWTShiroProperties;
import com.kk.ddd.support.shiro.JWTShiroWebAutoConfiguration;
import com.kk.ddd.support.shiro.JWTShiroWebFilterConfiguration;
import java.util.Collection;
import java.util.Map;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.filter.mgt.DefaultFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <br>
 *
 * @author KaiKoo
 */
@LiteConfiguration
@AutoConfigureBefore({JWTShiroWebAutoConfiguration.class, JWTShiroWebFilterConfiguration.class})
@Import({JWTShiroWebAutoConfiguration.class, JWTShiroWebFilterConfiguration.class})
@RestControllerAdvice
public class AuthConfiguration {

  // 登录认证异常
  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
  public Result<?> handleAuthenticationException(AuthenticationException e) {
    return Result.fail("Login failed!");
  }

  /**
   * 定义过滤链，使用LinkedHashMap。 <br>
   * 通过注入FilterRegistrationBean，加入了spring过滤器，且order默认为1。
   *
   * @see DefaultFilter 默认支持的过滤器类型
   */
  @Bean
  public ShiroFilterChainDefinition shiroFilterChainDefinition() {
    var filterChainDefinition = new DefaultShiroFilterChainDefinition();
    // 登出接口，LogoutFilter已实现，无需写controller，会重定向到"/"路径下。
    filterChainDefinition.addPathDefinition("/logout", DefaultFilter.logout.name());
    // 兜底认证拦截
    filterChainDefinition.addPathDefinition("/api/**", DefaultFilter.authc.name());
    return filterChainDefinition;
  }

  @Bean
  public SessionsSecurityManager securityManager(
      Collection<Realm> realms, Algorithm algorithm, JWTShiroProperties jwtProperties) {
    return new AuthSecurityManager(realms, algorithm, jwtProperties);
  }

  @Bean
  @RefreshScope
  public Realm adminRealm(@Value("#{${shiro.admin:{}}}") Map<String, String> admin) {
    return new AdminRealm(admin);
  }
}
