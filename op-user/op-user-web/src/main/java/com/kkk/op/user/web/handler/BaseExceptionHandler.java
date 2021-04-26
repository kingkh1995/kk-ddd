package com.kkk.op.user.web.handler;

import com.kkk.op.support.exception.BussinessException;
import java.time.DateTimeException;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    // 参数校验异常
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,
            ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(Exception exception) {
        log.warn("ValidationException =>", exception);
        if (exception instanceof MethodArgumentNotValidException) {
            return ((MethodArgumentNotValidException) exception).getBindingResult().getFieldError()
                    .getDefaultMessage();
        } else if (exception instanceof ConstraintViolationException) {
            return ((ConstraintViolationException) exception).getConstraintViolations()
                    .stream().findAny().get().getMessage();
        }
        return exception.getMessage();
    }

    @ExceptionHandler(BussinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public String handleBussinessException(BussinessException exception) {
        log.warn("BussinessException =>", exception);
        return exception.getMessage();
    }

}
