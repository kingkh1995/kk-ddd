package com.kkk.op.support.tools;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.validation.constraints.NotNull;

/**
 * 日期工具类
 * @author KaiKoo
 */
public final class DateUtil {

    private DateUtil() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static LocalDateTime toLocalDateTime(long epochSecond) {
        return toLocalDateTime(epochSecond, ZoneId.systemDefault());
    }

    public static LocalDateTime toLocalDateTime(long epochSecond, @NotNull ZoneId zoneId) {
        return Instant.ofEpochSecond(epochSecond).atZone(zoneId).toLocalDateTime();
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

    public static Timestamp toTimestamp(@NotNull LocalDateTime localDateTime,
            @NotNull ZoneId zoneId) {
        return Timestamp.from(localDateTime.atZone(zoneId).toInstant());
    }

    public static Timestamp toTimestamp(@NotNull @NotNull LocalDate localDate) {
        return toTimestamp(localDate, ZoneId.systemDefault());
    }

    public static Timestamp toTimestamp(@NotNull @NotNull LocalDate localDate,
            @NotNull ZoneId zoneId) {
        return Timestamp.from(localDate.atStartOfDay(zoneId).toInstant());
    }

    public static long toEpochSecond(@NotNull LocalDateTime localDateTime) {
        return toEpochSecond(localDateTime, ZoneId.systemDefault());
    }

    public static long toEpochSecond(@NotNull LocalDateTime localDateTime, @NotNull ZoneId zoneId) {
        return localDateTime.atZone(zoneId).toEpochSecond();
    }

    public static long toEpochSecond(@NotNull LocalDate localDate) {
        return toEpochSecond(localDate, ZoneId.systemDefault());
    }

    public static long toEpochSecond(@NotNull LocalDate localDate, @NotNull ZoneId zoneId) {
        return localDate.atStartOfDay(zoneId).toEpochSecond();
    }
}
