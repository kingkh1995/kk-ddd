package com.kk.ddd.support.exception;

/**
 * 外部服务异常 <br>
 *
 * @author KaiKoo
 */
public class ExternalServerException extends RuntimeException {

  public ExternalServerException(Throwable cause) {
    super(cause);
  }
}
