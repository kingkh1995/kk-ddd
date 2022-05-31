package com.kk.ddd.user.web.authc;

import com.kk.ddd.support.base.LocalRequestContextHolder;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.realm.SimpleAccountRealm;

/**
 * 配置管理员登录验证 <br>
 *
 * @author KaiKoo
 */
@Slf4j
public class AdminRealm extends SimpleAccountRealm {

  public AdminRealm(final Map<String, String> admin) {
    super("AdminRealm");
    log.info("admin = {}", admin);
    Optional.ofNullable(admin).orElse(Collections.emptyMap()).forEach(super::addAccount);
  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
      throws AuthenticationException {
    var account = super.doGetAuthenticationInfo(token);
    Optional.ofNullable(account).orElseThrow(UnknownAccountException::new);
    var requestContext = LocalRequestContextHolder.get();
    requestContext.setOperatorId(0L);
    requestContext.setClaims(
        Map.of("username", account.getPrincipals().getPrimaryPrincipal(), "name", "管理员"));
    log.info("requestContext => {}", requestContext);
    return account;
  }
}
