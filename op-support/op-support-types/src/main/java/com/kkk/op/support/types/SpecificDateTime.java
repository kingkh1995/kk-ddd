package com.kkk.op.support.types;

import com.kkk.op.support.exception.IllegalArgumentExceptions;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

/**
 * 特殊日期时间类DP基类 <br>
 *
 * @author KaiKoo
 */
public abstract class SpecificDateTime {

  protected final ZonedDateTime value;

  protected final boolean obtainTime;

  protected SpecificDateTime(
      @NotNull ZonedDateTime value,
      boolean obtainTime,
      String fieldName,
      @Nullable Instant now, // 当前时间戳作为参数传入
      Boolean future,
      Boolean includePresent) {
    if (value == null) {
      throw IllegalArgumentExceptions.forIsNull(fieldName);
    }
    if (now != null) {
      var zonedNow = now.atZone(value.getZone());
      if (!obtainTime) {
        zonedNow = zonedNow.with(LocalTime.MIDNIGHT);
      }
      if (!(includePresent && value.isEqual(zonedNow))) {
        if (future && value.isBefore(zonedNow)) {
          throw IllegalArgumentExceptions.requireFuture(fieldName, includePresent, obtainTime);
        } else if (!future && value.isAfter(zonedNow)) {
          throw IllegalArgumentExceptions.requirePast(fieldName, includePresent, obtainTime);
        }
      }
    }
    this.value = value;
    this.obtainTime = obtainTime;
  }

  public LocalDate toLocalDate() {
    return this.value.toLocalDate();
  }

  public LocalDateTime toLocalDateTime() {
    if (!this.obtainTime) {
      throw new UnsupportedOperationException();
    }
    return this.toLocalDateTime();
  }

  public ZoneId getZoneId() {
    return this.getZoneId();
  }
}
