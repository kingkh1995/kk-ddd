package com.kkk.op.user.web.handler;

import com.kkk.op.support.exception.BussinessException;
import java.time.DateTimeException;
import java.util.Collection;
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
 * todo... 待优化
 *
 * @author KaiKoo
 */
@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

  private static final String BAD_REQUEST_DEFAULT_MESSAGE = "请求参数不合法！";

  // 先决条件失败
  @ExceptionHandler({DateTimeException.class, IllegalArgumentException.class})
  @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
  public String handlePreconditionFailed(Exception exception) {
    log.warn("Precondition Failed =>", exception);
    return exception.getMessage();
  }

  // Controller层请求参数有误
  @ExceptionHandler({
    ConstraintViolationException.class, // 500 to 400
    BindException.class, // MethodArgumentNotValidException
    HttpMessageConversionException.class, //
    TypeMismatchException.class //
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleBadRequest(Exception exception) {
    log.warn("Bad Request =>", exception);
    var defaultMessage = BAD_REQUEST_DEFAULT_MESSAGE;
    if (exception instanceof ConstraintViolationException) {
      return Optional.ofNullable((ConstraintViolationException) exception)
          .map(ConstraintViolationException::getConstraintViolations)
          .map(Collection::stream)
          .orElse(Stream.empty())
          .findAny()
          .map(ConstraintViolation::getMessage)
          .orElse(defaultMessage);
    } else if (exception instanceof BindException) {
      return Optional.ofNullable(((BindException) exception))
          .map(BindException::getBindingResult)
          .map(Errors::getFieldError)
          .map(FieldError::getDefaultMessage)
          .orElse(defaultMessage);
    }
    return defaultMessage;
  }

  // 业务异常
  @ExceptionHandler(BussinessException.class)
  @ResponseStatus(HttpStatus.OK)
  public String handleBussinessException(BussinessException exception) {
    log.error("BussinessException =>", exception);
    return exception.getMessage();
  }

  // 兜底500异常
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handlesException(BussinessException exception) {
    log.error("Exception =>", exception);
    return "服务器开小差了，请稍后再试！";
  }
}
