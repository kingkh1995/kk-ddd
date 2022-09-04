package com.kk.ddd.user.web.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.kk.ddd.support.bean.LocalRequestContextHolder;
import com.kk.ddd.support.shiro.JWTShiroProperties;
import com.kk.ddd.support.shiro.JWTWebSecurityManager;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;

/**
 * 登录成功颁发JWT <br>
 *
 * @author KaiKoo
 */
@Slf4j
public class AuthSecurityManager extends JWTWebSecurityManager {

  private final Algorithm algorithm;

  private final JWTShiroProperties jwtProperties;

  public AuthSecurityManager(
      Collection<Realm> realms, Algorithm algorithm, JWTShiroProperties jwtProperties) {
    super(realms);
    this.algorithm = algorithm;
    this.jwtProperties = jwtProperties;
  }

  @Override
  protected void onSuccessfulLogin(
      AuthenticationToken token, AuthenticationInfo info, Subject subject) {
    super.onSuccessfulLogin(token, info, subject);
    // 使用账号密码登录则生成jwt-token并保存至http response header
    if (token instanceof UsernamePasswordToken) {
      storeToken(subject, createToken(info));
    }
  }

  private String createToken(AuthenticationInfo info) {
    var requestContext = LocalRequestContextHolder.get();
    var now = requestContext.getCommitTime().toInstant();
    var builder = JWT.create()
            .withIssuer(jwtProperties.getIssuer())
            .withIssuedAt(Date.from(now))
            .withExpiresAt(
                    Date.from(now.plus(jwtProperties.getExpiredAfterMinutes(), ChronoUnit.MINUTES)))
            .withKeyId(String.valueOf(requestContext.getOperatorId()))
            .withSubject((String) info.getPrincipals().getPrimaryPrincipal());
    requestContext.getClaims().forEach(builder::withClaim);
    return builder.sign(algorithm);
  }

  private void storeToken(Subject subject, String token) {
    Optional.ofNullable(WebUtils.getHttpResponse(subject))
        .ifPresent(response -> response.setHeader(jwtProperties.getTokenHeader(), token));
  }
}
