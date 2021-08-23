package com.kkk.op.support.fsm;

import com.kkk.op.support.exception.BusinessException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckResult {

  private boolean success;
  private String message;

  public static CheckResult success() {
    return new CheckResult(true, "success");
  }

  public static CheckResult fail(String message) {
    return new CheckResult(false, message);
  }

  public void throwIfFail() {
    if (this.success) {
      return;
    }
    throw new BusinessException(this.message);
  }
}
