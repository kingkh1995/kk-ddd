package com.kkk.op.support.bean;

import com.kkk.op.support.aspect.DegradedServiceAspect.DegradedServiceException;
import com.kkk.op.support.base.LocalRequestContextHolder;
import com.kkk.op.support.bean.IPControlInterceptor.IPControlBlockedException;
import com.kkk.op.support.exception.BusinessException;
import java.time.DateTimeException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * ResponseBody的请求要修改返回值只能通过ResponseBodyAdvice   <br>
 * 泛型需要指定，且会出现转换异常，注意返回值为String默认按视图解析。    <br>
 * 1、将所有响应包装为Result； 2、为所有响应添加额外信息（包括失败）。
 *
 * @author KaiKoo
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.kkk.op") // 实际是拦截@ResponseBody，故会拦截@ExceptionHandler方法
public class RestControllerResponseBodyAdvice implements ResponseBodyAdvice<Object> {

  // ===============================================================================================

  /** 全局异常处理 */

  // 先决条件失败
  @ExceptionHandler({DateTimeException.class, IllegalArgumentException.class})
  @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
  public Result<?> handlePreconditionFailed(Exception e) {
    log.warn("Precondition Failed =>", e);
    return Result.fail(e.getMessage());
  }

  // Controller层请求参数有误
  @ExceptionHandler({
    ConstraintViolationException.class, // 500 to 400
    BindException.class, // MethodArgumentNotValidException
    HttpMessageConversionException.class, //
    HttpMediaTypeException.class, //
    TypeMismatchException.class //
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Result<?> handleBadRequest(Exception e) {
    log.warn("Bad Request =>", e);
    var message = "请求参数不合法！";
    if (e instanceof ConstraintViolationException cve) {
      message =
          Stream.ofNullable(cve.getConstraintViolations())
              .flatMap(Set::stream)
              .findAny()
              .map(ConstraintViolation::getMessage)
              .orElse(message);
    } else if (e instanceof BindException be) {
      message =
          Optional.of(be.getBindingResult())
              .map(Errors::getFieldError)
              .map(FieldError::getDefaultMessage)
              .orElse(message);
    }
    return Result.fail(message);
  }

  // 业务异常
  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.OK)
  public Result<?> handleBusinessException(BusinessException e) {
    log.error("BusinessException =>", e);
    return Result.fail(e.getMessage());
  }

  // Optional异常 fixme... NO_CONTENT
  @ExceptionHandler(NoSuchElementException.class)
  @ResponseStatus(HttpStatus.OK) // NO_CONTENT
  public Result<?> handleNoSuchElementException(NoSuchElementException e) {
    log.error("NoSuchElementException =>", e);
    return Result.fail("No Content");
  }

  // IP-Control异常
  @ExceptionHandler(IPControlBlockedException.class)
  @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS) // 429
  public Result<?> handleIPControlBlockedException(IPControlBlockedException e) {
    return Result.fail("Too Many Requests");
  }

  // 登录认证异常
  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
  public Result<?> handleAuthenticationException(AuthenticationException e) {
    log.error("AuthenticationException =>", e);
    return Result.fail("Login failed!");
  }

  // degraded异常
  @ExceptionHandler(DegradedServiceException.class)
  @ResponseStatus(HttpStatus.I_AM_A_TEAPOT) // 418
  public Result<?> handleDegradedServiceException(DegradedServiceException e) {
    return Result.fail("I'm a teapot");
  }

  // 兜底500异常
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Result<?> handlesException(Exception e) {
    log.error("Exception =>", e);
    return Result.fail("服务器开小差了，请稍后再试！");
  }

  // ===============================================================================================

  @Override
  public boolean supports(
      MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    // 拦截范围内再次判断，要求响应格式为json
    return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
  }

  @Override
  public Object beforeBodyWrite(
      Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {
    if (returnType.hasMethodAnnotation(ExceptionHandler.class)) {
      // 异常拦截处理后可以再次处理，异常拦截类需要有@RestControllerAdvice注解，因为其包含@ResponseBody注解
      appendAdds((Result<?>) body);
      return body;
    }
    Result<?> result;
    if (body instanceof Result<?> r){
      result = r;
    } else {
      result = Result.succeed(body);
    }
    appendAdds(result);
    return result;
  }

  private void appendAdds(Result<?> result) {
    var context = LocalRequestContextHolder.get();
    if (context == null) {
      return;
    }
    result.append("traceId", context.getTraceId());
    result.append("commitTime", context.getCommitTime());
    result.append("cost", context.calculateCostMillis() + "ms");
  }
}
