package com.kkk.op.support.shiro;

import java.io.IOException;
import java.util.Objects;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.BearerToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;

/**
 * 登录验证过滤器，先判断是否登录成功（之前的过滤器中登录成功，其实不存在此场景），未登录成功则取出请求头中的token自动认证。
 *
 * @author KaiKoo
 */
@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticatingFilter extends AuthenticatingFilter {

  private final JWTShiroProperties jwtProperties;

  @Override
  protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
    // 在AuthenticatingFilter的executeLogin方法中被引用
    var httpRequest = WebUtils.toHttp(request);
    return Objects.nonNull(httpRequest)
        ? new BearerToken(httpRequest.getHeader(jwtProperties.getTokenHeader()))
        : null;
  }

  @SneakyThrows
  @Override
  protected boolean isAccessAllowed(
      ServletRequest request, ServletResponse response, Object mappedValue) {
    // 判断是否已登录成功，之前的过滤器中自动登录成功的场景。
    var accessAllowed = super.isAccessAllowed(request, response, mappedValue);
    // 如果已经自动成功，并且访问的是登录接口，则重定向到成功页面，不继续进行登录操作，防止登录多次。
    if (accessAllowed && isLoginRequest(request, response)) {
      issueSuccessRedirect(request, response);
    }
    return accessAllowed;
  }

  // 通行失败后处理逻辑
  @Override
  protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
      throws Exception {
    if (isLoginRequest(request, response)) {
      // 访问的是登录接口 => 放行，执行登录
      return true;
    } else {
      // 其他 => 自动认证
      return executeLogin(request, response);
    }
  }

  @Override
  protected boolean onLoginFailure(
      AuthenticationToken token,
      AuthenticationException e,
      ServletRequest request,
      ServletResponse response) {
    // 自动认证失败返回401
    var httpResponse = WebUtils.toHttp(response);
    httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401未登录
    httpResponse.setCharacterEncoding("UTF-8");
    httpResponse.setHeader("Content-Type", "application/json");
    try (var out = response.getWriter()) {
      out.print("please login!");
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return false;
  }
}
