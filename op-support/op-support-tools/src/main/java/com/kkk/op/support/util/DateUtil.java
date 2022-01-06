package com.kkk.op.support.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.validation.constraints.NotNull;

/**
 * 日期工具类 （默认当前时区） <br>
 * todo... 国际化
 *
 * @author KaiKoo
 */
public final class DateUtil {

  /** 静态DateTimeFormatter变量（优先使用DateTimeFormatter定义的常量） */
  public static final DateTimeFormatter LOCAL_DATE_TIME =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private DateUtil() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  // atZone
  public static ZonedDateTime atZone(LocalDate localDate, @NotNull ZoneId zoneId) {
    return localDate.atStartOfDay(zoneId);
  }

  public static ZonedDateTime atZone(LocalDateTime localDateTime, @NotNull ZoneId zoneId) {
    return localDateTime.atZone(zoneId);
  }

  public static ZonedDateTime atZone(long epochMilli, @NotNull ZoneId zoneId) {
    return Instant.ofEpochMilli(epochMilli).atZone(zoneId);
  }

  public static ZonedDateTime atZone(Timestamp timestamp, @NotNull ZoneId zoneId) {
    return timestamp.toInstant().atZone(zoneId);
  }

  // from epochMilli
  public static LocalDateTime toLocalDateTime(long epochMilli, @NotNull ZoneId zoneId) {
    return atZone(epochMilli, zoneId).toLocalDateTime();
  }

  public static LocalDateTime toLocalDateTime(long epochMilli) {
    return toLocalDateTime(epochMilli, ZoneId.systemDefault());
  }

  public static LocalDate toLocalDate(long epochMilli, @NotNull ZoneId zoneId) {
    return atZone(epochMilli, zoneId).toLocalDate();
  }

  public static LocalDate toLocalDate(long epochMilli) {
    return toLocalDate(epochMilli, ZoneId.systemDefault());
  }
  // -----------------------------------------------------------------------------------------------

  // to epochMilli
  public static long toEpochMilli(@NotNull LocalDateTime localDateTime, @NotNull ZoneId zoneId) {
    return atZone(localDateTime, zoneId).toInstant().toEpochMilli();
  }

  public static long toEpochMilli(@NotNull LocalDateTime localDateTime) {
    return toEpochMilli(localDateTime, ZoneId.systemDefault());
  }

  public static long toEpochMilli(@NotNull LocalDate localDate, @NotNull ZoneId zoneId) {
    return atZone(localDate, zoneId).toInstant().toEpochMilli();
  }

  public static long toEpochMilli(@NotNull LocalDate localDate) {
    return toEpochMilli(localDate, ZoneId.systemDefault());
  }
  // -----------------------------------------------------------------------------------------------

  // from Timestamp

  public static LocalDateTime toLocalDateTime(Timestamp timestamp, @NotNull ZoneId zoneId) {
    return atZone(timestamp, zoneId).toLocalDateTime();
  }

  public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
    return timestamp.toLocalDateTime();
  }

  public static LocalDate toLocalDate(Timestamp timestamp, @NotNull ZoneId zoneId) {
    return atZone(timestamp, zoneId).toLocalDate();
  }

  public static LocalDate toLocalDate(Timestamp timestamp) {
    return toLocalDate(timestamp, ZoneId.systemDefault());
  }
  // -----------------------------------------------------------------------------------------------

  // to Timestamp
  public static Timestamp toTimestamp(
      @NotNull LocalDateTime localDateTime, @NotNull ZoneId zoneId) {
    return Timestamp.from(atZone(localDateTime, zoneId).toInstant());
  }

  public static Timestamp toTimestamp(@NotNull LocalDateTime localDateTime) {
    // 也可以这种写法：Timestamp.valueOf(localDateTime);
    return toTimestamp(localDateTime, ZoneId.systemDefault());
  }

  public static Timestamp toTimestamp(
      @NotNull @NotNull LocalDate localDate, @NotNull ZoneId zoneId) {
    return Timestamp.from(atZone(localDate, zoneId).toInstant());
  }

  public static Timestamp toTimestamp(@NotNull @NotNull LocalDate localDate) {
    return toTimestamp(localDate, ZoneId.systemDefault());
  }
  // -----------------------------------------------------------------------------------------------

}
