package com.kkk.op.support.shiro;

import java.util.Collection;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.subject.WebSubjectContext;
import org.apache.shiro.web.subject.support.DefaultWebSubjectContext;

/**
 * 认证服务器颁发JWT token，通过http header读取token，且不使用会话管理而是使用LocalRequestContext。 <br>
 * JWT的优点是无状态，所以缺点就是一旦颁发无法失效，故其实不是适合做登录认证，最适合的场景是一次性信息凭证。
 *
 * @author KaiKoo
 */
@Slf4j
public class JWTWebSecurityManager extends SessionsSecurityManager implements WebSecurityManager {

  @Getter protected SubjectFactory subjectFactory;

  public JWTWebSecurityManager() {
    super();
    // 使用WebDelegatingSubject，会持有ServletRequest和ServletResponse。
    this.subjectFactory = new DefaultWebSubjectFactory();
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public JWTWebSecurityManager(Realm singleRealm) {
    this();
    setRealm(singleRealm);
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public JWTWebSecurityManager(Collection<Realm> realms) {
    this();
    setRealms(realms);
  }

  public boolean isHttpSessionMode() {
    return false;
  }

  @Override
  public Subject login(Subject subject, AuthenticationToken token) throws AuthenticationException {
    AuthenticationInfo info;
    info = authenticate(token);
    var loggedIn = createSubject(token, info, subject);
    onSuccessfulLogin(token, info, loggedIn);
    return loggedIn;
  }

  protected Subject createSubject(
      AuthenticationToken token, AuthenticationInfo info, Subject existing) {
    SubjectContext context = new DefaultWebSubjectContext();
    context.setAuthenticated(true);
    context.setAuthenticationToken(token);
    context.setAuthenticationInfo(info);
    context.setSecurityManager(this);
    if (existing != null) {
      context.setSubject(existing);
    }
    return createSubject(context);
  }

  protected void onSuccessfulLogin(
      AuthenticationToken token, AuthenticationInfo info, Subject subject) {
    // do nothing
  }

  @Override
  public Subject createSubject(SubjectContext subjectContext) {
    // create a copy so we don't modify the argument's backing map:
    SubjectContext context = new DefaultWebSubjectContext((WebSubjectContext) subjectContext);

    // ensure that the context has a SecurityManager instance, and if not, add one:
    context = ensureSecurityManager(context);

    return doCreateSubject(context);
  }

  protected Subject doCreateSubject(SubjectContext context) {
    return getSubjectFactory().createSubject(context);
  }

  protected SubjectContext ensureSecurityManager(SubjectContext context) {
    if (context.resolveSecurityManager() != null) {
      log.trace("Context already contains a SecurityManager instance.  Returning.");
      return context;
    }
    log.trace("No SecurityManager found in context.  Adding self reference.");
    context.setSecurityManager(this);
    return context;
  }

  @Override
  public void logout(Subject subject) {
    // jwt凭证无法登出
  }

  @Override
  public Session start(SessionContext context) {
    throw new UnsupportedOperationException("Create session operation is not supported!");
  }

  @Override
  public Session getSession(SessionKey key) throws SessionException {
    throw new UnsupportedOperationException("Get session operation is not supported!");
  }
}
