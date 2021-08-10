package com.kkk.op.support.exception;

/**
 * domain层业务异常 <br>
 *
 * @author KaiKoo
 */
public class BusinessException extends RuntimeException {

  public BusinessException(Throwable cause) {
    super(cause);
  }

  public BusinessException(String message) {
    super(message);
  }
}
