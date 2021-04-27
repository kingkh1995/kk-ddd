package com.kkk.op.user.web.handler;

import com.kkk.op.support.exception.BussinessException;
import java.net.BindException;
import java.time.DateTimeException;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * todo... 待优化
 * @author KaiKoo
 */
@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(DateTimeException.class)
    @ResponseStatus(HttpStatus.OK)
    public String handleDateTimeException(DateTimeException exception) {
        log.warn("DateTimeException =>", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    public String handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn("IllegalArgumentException =>", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.OK)
    public String handleValidationException(ValidationException exception) {
        log.warn("ValidationException =>", exception);
        return exception.getMessage();
    }

    // 参数校验异常 请求未进入controller
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,
            HttpMessageNotReadableException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(Exception exception) {
        log.warn("Bad Request =>", exception);
        if (exception instanceof MethodArgumentNotValidException) {
            return ((MethodArgumentNotValidException) exception).getBindingResult().getFieldError()
                    .getDefaultMessage();
        }
        if (exception instanceof ConstraintViolationException) {
            return ((ConstraintViolationException) exception).getConstraintViolations()
                    .stream().findAny().get().getMessage();
        }
        return "请求参数不合法！";
    }

    @ExceptionHandler(BussinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public String handleBussinessException(BussinessException exception) {
        log.warn("BussinessException =>", exception);
        return exception.getMessage();
    }

}
