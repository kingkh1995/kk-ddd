package com.kk.ddd.support.access;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class AccessConditionCheckException extends RuntimeException {

  public AccessConditionCheckException(String message) {
    super(message);
  }

  public AccessConditionCheckException(Throwable cause) {
    super(cause);
  }
}
