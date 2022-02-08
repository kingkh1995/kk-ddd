package com.kkk.op.support.constant;

import java.time.format.DateTimeFormatter;

/**
 * 日期相关常量类 <br>
 *
 * @author KaiKoo
 */
public final class DateConstants {
  private DateConstants() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  /** 静态DateTimeFormatter变量（优先使用DateTimeFormatter定义的常量） */
  public static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
