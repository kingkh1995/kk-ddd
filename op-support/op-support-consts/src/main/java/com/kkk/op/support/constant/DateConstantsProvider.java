package com.kkk.op.support.constant;

import java.time.format.DateTimeFormatter;

/**
 * date constants spi provider <br>
 *
 * @author KaiKoo
 */
public interface DateConstantsProvider {

  default DateTimeFormatter getDefaultDateTimeFormatter() {
    return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  }
}
