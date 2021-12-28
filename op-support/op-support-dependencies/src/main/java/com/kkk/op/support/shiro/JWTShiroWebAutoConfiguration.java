package com.kkk.op.support.shiro;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.boot.autoconfigure.ShiroAutoConfiguration;
import org.apache.shiro.spring.config.web.autoconfigure.ShiroWebAutoConfiguration;
import org.apache.shiro.spring.config.web.autoconfigure.ShiroWebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;

/**
 * shiro自动配置类
 *
 * @author KaiKoo
 */
@AutoConfigureBefore(ShiroAutoConfiguration.class)
@AutoConfigureAfter(ShiroWebMvcAutoConfiguration.class)
@EnableAutoConfiguration(exclude = ShiroWebAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(JWTShiroProperties.class)
@ConditionalOnProperty(name = "shiro.web.enabled", matchIfMissing = true)
@RequiredArgsConstructor
public class JWTShiroWebAutoConfiguration extends ShiroWebAutoConfiguration {

  private final JWTShiroProperties jwtProperties;

  @Bean
  @ConditionalOnMissingBean
  @Override
  protected SessionsSecurityManager securityManager(List<Realm> realms) {
    return new JWTWebSecurityManager(realms);
  }

  @Bean
  @RefreshScope
  @ConditionalOnMissingBean
  public Algorithm algorithm() {
    return Algorithm.HMAC256(jwtProperties.getSecretKey());
  }

  @Bean
  @RefreshScope
  @ConditionalOnMissingBean
  public JWTVerifier jwtVerifier(Algorithm algorithm) {
    return JWT.require(algorithm).withIssuer(jwtProperties.getIssuer()).build();
  }

  @Bean
  public Realm jwtRealm(JWTVerifier verifier) {
    return new JWTRealm(verifier);
  }
}
