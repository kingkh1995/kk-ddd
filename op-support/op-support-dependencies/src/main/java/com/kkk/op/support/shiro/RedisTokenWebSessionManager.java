package com.kkk.op.support.shiro;

import java.io.Serializable;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.DelegatingSession;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.apache.shiro.web.session.mgt.WebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于token认证的自定义SessionManager 将sessionid作为token添加到请求头中进行认证 todo. session保存用户登录信息
 *
 * @author KaiKoo
 */
public class RedisTokenWebSessionManager extends DefaultSessionManager
    implements WebSessionManager {

  public RedisTokenWebSessionManager() {
    // 配置session超时时间（毫秒）
    this.setGlobalSessionTimeout(1000 * 60 * 30); // 30分钟
    // todo...使用redis进行会话管理
  }

  private static final Logger log = LoggerFactory.getLogger(RedisTokenWebSessionManager.class);

  private static final String HEADER_NAME = "HM_Token";

  private void storeSessionId(Serializable currentId, HttpServletResponse response) {
    if (currentId == null) {
      var msg = "sessionId cannot be null when persisting for subsequent requests.";
      throw new IllegalArgumentException(msg);
    }
    var idString = currentId.toString();
    response.setHeader(HEADER_NAME, idString);
    log.trace("Set session ID cookie for session with id {}", idString);
  }

  private Serializable getReferencedSessionId(ServletRequest request) {
    if (!(request instanceof HttpServletRequest)) {
      log.debug("Current request is not an HttpServletRequest.  Returning null.");
      return null;
    }
    var id = ((HttpServletRequest) request).getHeader(HEADER_NAME);
    if (id != null) {
      request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
      // automatically mark it valid here.  If it is invalid, the
      // onUnknownSession method below will be invoked and we'll remove the attribute at that time.
      request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
    }
    return id;
  }

  @Override
  protected void onStart(Session session, SessionContext context) {
    super.onStart(session, context);
    if (!WebUtils.isHttp(context)) {
      log.debug(
          "SessionContext argument is not HTTP compatible or does not have an HTTP request/response pair.");
      return;
    }
    var request = WebUtils.getHttpRequest(context);
    var response = WebUtils.getHttpResponse(context);
    var sessionId = session.getId();
    storeSessionId(sessionId, response);
    request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_IS_NEW, Boolean.TRUE);
  }

  @Override
  public Serializable getSessionId(SessionKey key) {
    var id = super.getSessionId(key);
    if (id == null) {
      id = getReferencedSessionId(WebUtils.getRequest(key));
    }
    return id;
  }

  @Override
  protected void onExpiration(Session s, ExpiredSessionException ese, SessionKey key) {
    super.onExpiration(s, ese, key);
    onInvalidation(key);
  }

  @Override
  protected void onInvalidation(Session session, InvalidSessionException ise, SessionKey key) {
    super.onInvalidation(session, ise, key);
    onInvalidation(key);
  }

  private void onInvalidation(SessionKey key) {
    var request = WebUtils.getRequest(key);
    if (request != null) {
      request.removeAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID);
    }
    // do nothing to response header
  }

  @Override
  protected void onStop(Session session, SessionKey key) {
    super.onStop(session, key);
    log.debug("Session has been stopped (subject logout or explicit stop).");
    // do nothing to response header
  }

  protected Session createExposedSession(Session session, SessionContext context) {
    if (!WebUtils.isHttp(context)) {
      log.debug(
          "SessionContext argument is not HTTP compatible or does not have an HTTP request/response pair.");
      return null;
    }
    var request = WebUtils.getRequest(context);
    var response = WebUtils.getResponse(context);
    var key = new WebSessionKey(session.getId(), request, response);
    return new DelegatingSession(this, key);
  }

  protected Session createExposedSession(Session session, SessionKey key) {
    if (!WebUtils.isHttp(key)) {
      log.debug(
          "SessionContext argument is not HTTP compatible or does not have an HTTP request/response pair.");
      return null;
    }
    var request = WebUtils.getRequest(key);
    var response = WebUtils.getResponse(key);
    var sessionKey = new WebSessionKey(session.getId(), request, response);
    return new DelegatingSession(this, sessionKey);
  }

  /**
   * This is a native session manager implementation, so this method returns {@code false} always.
   *
   * @return {@code false} always
   */
  public boolean isServletContainerSessions() {
    return false;
  }
}
