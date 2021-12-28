package com.kkk.op.user.web.authc;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.kkk.op.support.model.dto.UserAuthcInfo;
import com.kkk.op.support.shiro.JWTShiroProperties;
import com.kkk.op.support.shiro.JWTWebSecurityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;

/**
 * 登录成功颁发JWT <br>
 *
 * @author KaiKoo
 */
@Slf4j
public class AuthcSecurityManager extends JWTWebSecurityManager {

  private final Algorithm algorithm;

  private final JWTShiroProperties jwtProperties;

  public AuthcSecurityManager(
      Collection<Realm> realms, Algorithm algorithm, JWTShiroProperties jwtProperties) {
    super(realms);
    this.algorithm = algorithm;
    this.jwtProperties = jwtProperties;
  }

  @Override
  protected void onSuccessfulLogin(
      AuthenticationToken token, AuthenticationInfo info, Subject subject) {
    super.onSuccessfulLogin(token, info, subject);
    if(info instanceof UserAuthenticationInfo userAuthenticationInfo){
        storeToken(subject, createToken(userAuthenticationInfo.getUserAuthcInfo()));
    }
  }

  private String createToken(UserAuthcInfo userAuthcInfo) {
    var now = Instant.now();
    return JWT.create()
        .withIssuer(jwtProperties.getIssuer())
        .withIssuedAt(Date.from(now))
        .withExpiresAt(
            Date.from(now.plus(jwtProperties.getExpiredAfterMinutes(), ChronoUnit.MINUTES)))
        .withKeyId(String.valueOf(userAuthcInfo.getId()))
        .withSubject(userAuthcInfo.getUsername())
        .withClaim("name", userAuthcInfo.getName())
        .sign(algorithm);
  }

  private void storeToken(Subject subject, String token) {
    var httpResponse = WebUtils.getHttpResponse(subject);
    if (httpResponse != null) {
      httpResponse.setHeader(jwtProperties.getTokenHeader(), token);
    }
  }
}
