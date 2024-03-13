package com.kk.ddd.support.util.task;

/**
 * 校验器返回结果 <br>
 *
 * @author KaiKoo
 */
public record TaskResult(boolean succeeded, String message) {

  private static final TaskResult SUCCEEDED = new TaskResult(true, "succeeded");

  public static TaskResult succeed() {
    return SUCCEEDED;
  }

  public static TaskResult fail(final String message) {
    return new TaskResult(false, message);
  }

  public boolean failed() {
    return !succeeded();
  }

  public void throwIfFail() {
    if (failed()) {
      throw new RuntimeException(this.message);
    }
  }

  public void throwCheckedIfFail() throws Throwable {
    if (failed()) {
      throw new Exception(this.message);
    }
  }
}
