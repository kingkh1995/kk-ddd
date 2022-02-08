package com.kkk.op.support.shiro;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.kkk.op.support.base.LocalRequestContextHolder;
import java.time.Instant;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.BearerToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.realm.AuthenticatingRealm;

/**
 * 供子项目使用，子项目只能解析JWT token。 <br>
 *
 * @author KaiKoo
 */
@Slf4j
public class JWTRealm extends AuthenticatingRealm {

  private final JWTVerifier jwtVerifier;

  public JWTRealm(JWTVerifier jwtVerifier) {
    super();
    super.setName("JWTRealm");
    // 使用BearerToken传递token
    super.setAuthenticationTokenClass(BearerToken.class);
    // 使用AllowAllCredentialsMatcher，只要正常返回就表示认证通过。
    super.setCredentialsMatcher(new AllowAllCredentialsMatcher());
    this.jwtVerifier = jwtVerifier;
  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
      throws AuthenticationException {
    var requestContext = LocalRequestContextHolder.get();
    var decodedJWT =
        decode(((BearerToken) token).getToken(), requestContext.getCommitTime().toInstant());
    save2LocalRequestContext(decodedJWT);
    return new SimpleAuthenticationInfo(decodedJWT.getSubject(), null, getName());
  }

  private DecodedJWT decode(String token, Instant commitTime) throws AuthenticationException {
    if (token == null || token.isBlank()) {
      throw new IncorrectCredentialsException();
    }
    DecodedJWT decodedJWT;
    try {
      decodedJWT = jwtVerifier.verify(token);
    } catch (JWTVerificationException e) {
      log.warn("verify failed!");
      throw new IncorrectCredentialsException();
    }
    // 判断凭证是否过期
    if (commitTime.isAfter(decodedJWT.getExpiresAt().toInstant())) {
      log.warn("expired at {}!", decodedJWT.getExpiresAt().toInstant());
      throw new ExpiredCredentialsException();
    }
    return decodedJWT;
  }

  private void save2LocalRequestContext(DecodedJWT decodedJWT) {
    var requestContext = LocalRequestContextHolder.get();
    requestContext.setOperatorId(Long.valueOf(decodedJWT.getKeyId()));
    var claims = decodedJWT.getClaims();
    requestContext.setClaims(
        Map.of("username", decodedJWT.getSubject(), "name", claims.get("name").asString()));
    log.info("requestContext => {}", requestContext);
  }
}
