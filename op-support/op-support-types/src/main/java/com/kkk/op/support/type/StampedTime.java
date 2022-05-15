package com.kkk.op.support.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.kkk.op.support.exception.IllegalArgumentExceptions;
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
public class StampedTime extends SpecificZonedDateTime implements Comparable<StampedTime> {

  protected StampedTime(@NotNull ZonedDateTime value, String fieldName, Instant current) {
    super(value, true, fieldName, current, false, true);
  }

  // of方法不需要校验 now参数默认为null
  @JsonCreator
  public static StampedTime of(@NotNull ZonedDateTime value) {
    return new StampedTime(value, "StampedTime", null);
  }

  public static StampedTime from(@NotNull Instant instant) {
    return of(instant.atZone(ZoneId.systemDefault()));
  }

  public static StampedTime current() {
    return of(ZonedDateTime.now());
  }

  public static StampedTime valueOf(ZonedDateTime value, String fieldName, Instant current) {
    if (value == null) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    if (current == null) {
      throw IllegalArgumentExceptions.forIsNull("current");
    }
    return new StampedTime(value, fieldName, current);
  }

  public static StampedTime valueOf(ZonedDateTime value, String fieldName) {
    return valueOf(value, fieldName, Instant.now());
  }

  @Override
  public int compareTo(StampedTime o) {
    return this.toZonedDateTime().compareTo(o.toZonedDateTime());
  }
}
