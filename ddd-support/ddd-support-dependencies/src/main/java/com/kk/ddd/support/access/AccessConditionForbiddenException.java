package com.kk.ddd.support.access;

import com.kk.ddd.support.exception.BusinessException;

/**
 * AccessCondition检查禁止访问，属于业务异常，故继承BusinessException <br>
 *
 * @author KaiKoo
 */
public class AccessConditionForbiddenException extends BusinessException {

  public static final AccessConditionForbiddenException INSTANCE =
      new AccessConditionForbiddenException();

  private AccessConditionForbiddenException() {
    super("Forbidden!");
  }
}
