package com.kkk.op.support.exception;

/**
 * domain层业务异常 <br>
 * todo... 待设计
 *
 * @author KaiKoo
 */
public class BussinessException extends RuntimeException {

  public BussinessException(Throwable cause) {
    super(cause);
  }

  public BussinessException(String message) {
    super(message);
  }
}
