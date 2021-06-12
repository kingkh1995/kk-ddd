package com.kkk.op.user.web.handler;

import com.kkk.op.support.exception.BussinessException;
import java.net.BindException;
import java.time.DateTimeException;
import java.util.Optional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * todo... 待优化
 *
 * @author KaiKoo
 */
@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

  // 先决条件失败
  @ExceptionHandler({
    DateTimeException.class,
    ValidationException.class,
    IllegalArgumentException.class
  })
  @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
  public String handlePreconditionFailed(Exception exception) {
    log.warn("Precondition Failed =>", exception);
    return exception.getMessage();
  }

  // 参数校验异常 请求未进入controller
  @ExceptionHandler({
    MethodArgumentNotValidException.class,
    ConstraintViolationException.class,
    HttpMessageNotReadableException.class,
    BindException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleBadRequest(Exception exception) {
    log.warn("Bad Request =>", exception);
    var defaultMessage = "请求参数不合法！";
    if (exception instanceof MethodArgumentNotValidException) {
      return Optional.ofNullable(
              ((MethodArgumentNotValidException) exception).getBindingResult().getFieldError())
          .map(FieldError::getDefaultMessage)
          .orElse(defaultMessage);
    }
    if (exception instanceof ConstraintViolationException) {
      return ((ConstraintViolationException) exception)
          .getConstraintViolations().stream()
              .findAny()
              .map(ConstraintViolation::getMessage)
              .orElse(defaultMessage);
    }
    return defaultMessage;
  }

  @ExceptionHandler(BussinessException.class)
  @ResponseStatus(HttpStatus.OK)
  public String handleBussinessException(BussinessException exception) {
    log.warn("BussinessException =>", exception);
    return exception.getMessage();
  }
}
