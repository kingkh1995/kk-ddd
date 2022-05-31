package com.kk.ddd.support.access;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class AccessConditionHelper {

  private AccessConditionHelper() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  private static final ThreadLocal<String> holder = new ThreadLocal<>();

  // 回放，并返回当前的备份
  public static String replay(String captured) {
    var backup = holder.get();
    holder.set(captured);
    return backup;
  }

  // 获取抓取的accessCondition
  public static String get() {
    return holder.get();
  }

  // 恢复回放前的备份
  public static void restore(String backup) {
    holder.set(backup);
  }
}
