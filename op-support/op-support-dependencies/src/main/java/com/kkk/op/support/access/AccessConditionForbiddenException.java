package com.kkk.op.support.access;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class AccessConditionForbiddenException extends RuntimeException {

  public static final AccessConditionForbiddenException INSTANCE =
      new AccessConditionForbiddenException();

  private AccessConditionForbiddenException() {
    super("Forbidden!");
  }
}
