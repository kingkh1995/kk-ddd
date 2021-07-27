package com.kkk.op.support.types;

import com.kkk.op.support.exception.IllegalArgumentExceptions;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

/**
 * 特殊日期时间类DP基类 <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
public abstract class SpecificDateTime {

  protected final ZonedDateTime value;

  protected final boolean obtainTime;

  protected SpecificDateTime(
      @NotNull ZonedDateTime value,
      boolean obtainTime,
      String fieldName,
      @Nullable Instant current, // 当前时间戳作为参数传入
      Boolean future,
      Boolean includePresent) {
    // 忽略毫秒值
    if (current != null) {
      // 先对比日期 再对比时间 且对比时只对比到秒
      var zonedCurrent = current.atZone(value.getZone());
      var cmp = value.toLocalDate().compareTo(zonedCurrent.toLocalDate());
      // 包含时间并且日期相等继续对比时间
      if (obtainTime && cmp == 0) {
        cmp = value.toLocalTime().withNano(0).compareTo(zonedCurrent.toLocalTime().withNano(0));
      }
      if (!(includePresent && cmp == 0)) {
        if (future && cmp < 1) {
          throw IllegalArgumentExceptions.requireFuture(fieldName, includePresent, obtainTime);
        } else if (!future && cmp > -1) {
          throw IllegalArgumentExceptions.requirePast(fieldName, includePresent, obtainTime);
        }
      }
    }
    this.value = value;
    this.obtainTime = obtainTime;
  }

  public ZoneId getZone() {
    return this.value.getZone();
  }

  public LocalDate toLocalDate() {
    return this.value.toLocalDate();
  }

  public LocalDateTime toLocalDateTime() {
    if (!this.obtainTime) {
      throw new UnsupportedOperationException();
    }
    return this.value.toLocalDateTime();
  }
}
