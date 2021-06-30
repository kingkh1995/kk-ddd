package com.kkk.op.support.enums;

import com.kkk.op.support.tools.DateUtil;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 日期格式枚举 <br>
 * （默认当前时区）<br>
 * todo... 国际化
 *
 * <p>失败抛出 java.time.DateTimeException
 *
 * @author KaiKoo
 */
@AllArgsConstructor
public enum DatePattern {
  EPOCH_MILLI(null, null, true),
  BASIC_DATE(DateTimeFormatter.BASIC_ISO_DATE, "yyMMdd", false),
  LOCAL_DATE(DateTimeFormatter.ISO_LOCAL_DATE, "yyyy-MM-dd", false),
  LOCAL_DATE_TIME(DateUtil.LOCAL_DATE_TIME, "yyyy-MM-dd HH:mm:ss", true),
  ;

  @Getter private final DateTimeFormatter formatter;

  @Getter private final String pattern;

  private final boolean obtainTime;

  public LocalDateTime parseLocalDateTime(String text) {
    if (text == null || text.isBlank()) {
      return null;
    }
    text = text.strip();
    if (this.formatter != null) {
      if (!this.obtainTime) { // 必须包含Time
        throw new DateTimeException("should obtain time");
      }
      try {
        return LocalDateTime.parse(text, this.formatter);
      } catch (DateTimeException e) {
        throw new DateTimeException("could not be parsed by pattern " + this.pattern, e);
      }
    }
    //  parse epochMilli
    long epochMilli;
    try {
      epochMilli = Long.parseLong(text);
    } catch (NumberFormatException e) {
      // 转为DateTimeException
      throw new DateTimeException("should be long", e);
    }
    return DateUtil.toLocalDateTime(epochMilli);
  }

  public LocalDate parseLocalDate(String text) {
    if (text == null || text.isBlank()) {
      return null;
    }
    text = text.strip();
    if (this.formatter != null) {
      if (this.obtainTime) { // 不能包含Time
        throw new DateTimeException("should not obtain time");
      }
      try {
        return LocalDate.parse(text, this.formatter);
      } catch (DateTimeException e) {
        throw new DateTimeException("could not be parsed by pattern " + this.pattern, e);
      }
    }
    //  parse epochMilli
    long epochMilli;
    try {
      epochMilli = Long.parseLong(text);
    } catch (NumberFormatException e) {
      // 转为DateTimeException
      throw new DateTimeException("should be long", e);
    }
    return DateUtil.toLocalDate(epochMilli);
  }

  public String format(LocalDateTime localDateTime) {
    if (localDateTime == null) {
      return null;
    }
    if (this.formatter != null) {
      return localDateTime.format(this.formatter);
    }
    // format epochMilli
    return String.valueOf(DateUtil.toEpochMilli(localDateTime));
  }

  public String format(LocalDate localDate) {
    if (localDate == null) {
      return null;
    }
    if (this.formatter != null) {
      if (!this.obtainTime) { // 不能包含Time
        throw new DateTimeException("should not obtain time");
      }
      return localDate.format(this.formatter);
    }
    // format epochMilli
    return String.valueOf(DateUtil.toEpochMilli(localDate));
  }
}
