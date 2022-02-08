package com.kkk.op.support.fsm;

import com.kkk.op.support.exception.BusinessException;

/**
 * 校验器返回结果 <br>
 *
 * @author KaiKoo
 */
public record CheckResult(boolean succeeded, String message) {

  public static CheckResult succeed() {
    return new CheckResult(true, "succeeded");
  }

  public static CheckResult fail(String message) {
    return new CheckResult(false, message);
  }

  public void throwIfFail() {
    if (!succeeded()) {
      throw new BusinessException(this.message);
    }
  }
}
