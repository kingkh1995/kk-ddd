package com.kkk.op.support.tools;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.validation.constraints.NotNull;

/**
 * 日期工具类 （默认当前时区） <br>
 * todo... 国际化
 *
 * @author KaiKoo
 */
public final class DateUtil {

  /** 静态DateTimeFormatter变量 */
  public static final DateTimeFormatter yyyy_MM_dd_HH_mm_ss =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public static final DateTimeFormatter yyyy_MM_dd_HH_mm_ss_SSS =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

  private DateUtil() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  /** 静态方法 */
  public static LocalDateTime toLocalDateTime(long epochMilli) {
    return toLocalDateTime(epochMilli, ZoneId.systemDefault());
  }

  public static LocalDateTime toLocalDateTime(long epochMilli, @NotNull ZoneId zoneId) {
    return Instant.ofEpochMilli(epochMilli).atZone(zoneId).toLocalDateTime();
  }

  public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
    return toLocalDateTime(timestamp, ZoneId.systemDefault());
  }

  public static LocalDateTime toLocalDateTime(Timestamp timestamp, @NotNull ZoneId zoneId) {
    return timestamp.toInstant().atZone(zoneId).toLocalDateTime();
  }

  public static Timestamp toTimestamp(@NotNull LocalDateTime localDateTime) {
    return toTimestamp(localDateTime, ZoneId.systemDefault());
  }

  public static Timestamp toTimestamp(
      @NotNull LocalDateTime localDateTime, @NotNull ZoneId zoneId) {
    return Timestamp.from(localDateTime.atZone(zoneId).toInstant());
  }

  public static Timestamp toTimestamp(@NotNull @NotNull LocalDate localDate) {
    return toTimestamp(localDate, ZoneId.systemDefault());
  }

  public static Timestamp toTimestamp(
      @NotNull @NotNull LocalDate localDate, @NotNull ZoneId zoneId) {
    return Timestamp.from(localDate.atStartOfDay(zoneId).toInstant());
  }

  public static long toEpochMilli(@NotNull LocalDateTime localDateTime) {
    return toEpochMilli(localDateTime, ZoneId.systemDefault());
  }

  public static long toEpochMilli(@NotNull LocalDateTime localDateTime, @NotNull ZoneId zoneId) {
    return localDateTime.atZone(zoneId).toInstant().toEpochMilli();
  }

  public static long toEpochMilli(@NotNull LocalDate localDate) {
    return toEpochMilli(localDate, ZoneId.systemDefault());
  }

  public static long toEpochMilli(@NotNull LocalDate localDate, @NotNull ZoneId zoneId) {
    return localDate.atStartOfDay(zoneId).toInstant().toEpochMilli();
  }
}
