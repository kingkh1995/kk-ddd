package com.kkk.op.support.exception;

import com.kkk.op.support.constant.Constants;

/**
 * domain层业务异常 <br>
 *
 * @author KaiKoo
 */
public class BusinessException extends RuntimeException {

  private final String code;

  public String getCode() {
    return code;
  }

  public BusinessException(String message) {
    this(Constants.BASE.getDefaultFailCode(), message);
  }

  public BusinessException(String code, String message) {
    super(message);
    this.code = code;
  }
}
