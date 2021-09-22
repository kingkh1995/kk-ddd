package com.kkk.op.support.access;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class AccessConditionHelper {

  private AccessConditionHelper() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  private static ThreadLocal<String> helper = new ThreadLocal<>();

  // 抓取accessCondition，不允许覆盖
  public static void capture(String accessCondition) {
    if (helper.get() == null) {
      helper.set(accessCondition);
    }
  }

  // 回放抓取的accessCondition，回放完清空，以便多次使用
  public static String replay() {
    var accessCondition = helper.get();
    helper.remove();
    return accessCondition;
  }

  // 手动清空（执行异常时）
  public static void reset() {
    helper.remove();
  }
}
