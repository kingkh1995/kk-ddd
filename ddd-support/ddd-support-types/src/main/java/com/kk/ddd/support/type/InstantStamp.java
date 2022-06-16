package com.kk.ddd.support.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.kk.ddd.support.util.IllegalArgumentExceptions;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * 时间戳 （操作时间、创建时间、更新时间等） <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true)
public class InstantStamp extends SpecificZonedDateTime implements Comparable<InstantStamp> {

  protected InstantStamp(@NotNull ZonedDateTime value, String fieldName, Instant current) {
    super(value, true, fieldName, current, false, true);
  }

  // of方法不需要校验 now参数默认为null
  @JsonCreator
  public static InstantStamp of(@NotNull ZonedDateTime value) {
    return new InstantStamp(value, "InstantStamp", null);
  }

  public static InstantStamp from(@NotNull Instant instant) {
    return of(instant.atZone(ZoneId.systemDefault()));
  }

  public static InstantStamp current() {
    return of(ZonedDateTime.now());
  }

  public static InstantStamp valueOf(ZonedDateTime value, String fieldName, Instant current) {
    if (value == null) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    if (current == null) {
      throw IllegalArgumentExceptions.forIsNull("current");
    }
    return new InstantStamp(value, fieldName, current);
  }

  public static InstantStamp valueOf(ZonedDateTime value, String fieldName) {
    return valueOf(value, fieldName, Instant.now());
  }

  @Override
  public int compareTo(InstantStamp o) {
    return this.toZonedDateTime().compareTo(o.toZonedDateTime());
  }
}
