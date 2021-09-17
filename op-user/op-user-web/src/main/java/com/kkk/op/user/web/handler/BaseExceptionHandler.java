package com.kkk.op.user.web.handler;

import com.kkk.op.support.accessCondition.AccessConditionForbiddenException;
import com.kkk.op.support.bean.IPControlInterceptor.IPControlBlockedException;
import com.kkk.op.support.bean.Result;
import com.kkk.op.support.exception.BusinessException;
import java.time.DateTimeException;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理拦截器
 *
 * @author KaiKoo
 */
@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

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
    TypeMismatchException.class //
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Result<?> handleBadRequest(Exception e) {
    log.warn("Bad Request =>", e);
    var message = "请求参数不合法！";
    if (e instanceof ConstraintViolationException) {
      message =
          Optional.ofNullable((ConstraintViolationException) e)
              .map(ConstraintViolationException::getConstraintViolations)
              .map(Collection::stream)
              .orElse(Stream.empty())
              .findAny()
              .map(ConstraintViolation::getMessage)
              .orElse(message);
    } else if (e instanceof BindException) {
      message =
          Optional.ofNullable(((BindException) e))
              .map(BindException::getBindingResult)
              .map(Errors::getFieldError)
              .map(FieldError::getDefaultMessage)
              .orElse(message);
    }
    return Result.fail(message);
  }

  // 业务异常
  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.OK)
  public Result<?> handleBussinessException(BusinessException e) {
    log.error("BussinessException =>", e);
    return Result.fail(e.getMessage());
  }

  // Optional异常
  @ExceptionHandler(NoSuchElementException.class)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Result<?> handleNoSuchElementException(NoSuchElementException e) {
    log.error("NoSuchElementException =>", e);
    return Result.fail("No Content");
  }

  // accessCondition异常
  @ExceptionHandler(AccessConditionForbiddenException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Result<?> handleNoSuchElementException(AccessConditionForbiddenException e) {
    return Result.fail("Forbidden");
  }

  // IP-Control异常
  @ExceptionHandler(IPControlBlockedException.class)
  @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS) // 429
  public Result<?> handleIPControlBlockedException(IPControlBlockedException e) {
    return Result.fail("Too Many Requests");
  }

  // 兜底500异常
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Result<?> handlesException(Exception e) {
    log.error("Exception =>", e);
    return Result.fail("服务器开小差了，请稍后再试！");
  }
}
