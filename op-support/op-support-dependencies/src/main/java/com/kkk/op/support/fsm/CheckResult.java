package com.kkk.op.support.fsm;

import com.kkk.op.support.exception.BusinessException;

/**
 * 校验器返回结果 <br>
 *
 * @author KaiKoo
 */
public record CheckResult(boolean successed, String message) {

  public static CheckResult success() {
    return new CheckResult(true, "success");
  }

  public static CheckResult fail(String message) {
    return new CheckResult(false, message);
  }

  public void throwIfFail() {
    if (!successed()) {
      throw new BusinessException(this.message);
    }
  }
}
