package com.kkk.op.support.constant;

import java.time.format.DateTimeFormatter;

/**
 * base constants spi provider <br>
 *
 * @author KaiKoo
 */
public interface BaseConstantsProvider {

  default DateTimeFormatter dateTimeFormatter() {
    return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  }

  default String succeedCode() {
    return "0";
  }

  default String succeedMessage() {
    return "ok";
  }

  default String failCode() {
    return "1";
  }
}
