package com.kkk.op.support.fsm;

import com.kkk.op.support.exception.BusinessException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 校验器返回结果 <br>
 *
 * @author KaiKoo
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckResult {

  @Getter private boolean successed;
  @Getter private String message;

  public static CheckResult success() {
    return new CheckResult(true, "success");
  }

  public static CheckResult fail(String message) {
    return new CheckResult(false, message);
  }

  public boolean isFailed() {
    return !this.successed;
  }

  public void throwIfFail() {
    if (this.isFailed()) {
      throw new BusinessException(this.message);
    }
  }
}
