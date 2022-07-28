package com.kk.ddd.support.type;

import com.fasterxml.jackson.annotation.JsonValue;
import com.kk.ddd.support.core.Type;
import com.kk.ddd.support.util.IllegalArgumentExceptions;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

/**
 * 特殊日期时间类DP基类 <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
public abstract class SpecificZonedDateTime implements Type {

  @JsonValue protected final ZonedDateTime value;

  protected final boolean obtainTime;

  protected SpecificZonedDateTime(
      @NotNull ZonedDateTime value,
      boolean obtainTime,
      String fieldName,
      @Nullable Instant current, // 当前时间戳作为参数传入
      Boolean future,
      Boolean presentInclusive) {
    // current存在才对比（兼容读取值），且忽略毫秒值。
    if (current != null) {
      // 先对比日期 再对比时间
      var zonedCurrent = current.atZone(value.getZone());
      var cmp = value.toLocalDate().compareTo(zonedCurrent.toLocalDate());
      // 包含时间并且日期相等 则继续对比时间
      if (obtainTime && cmp == 0) {
        // 对比时只对比到秒
        cmp =
            Integer.compare(
                value.toLocalTime().toSecondOfDay(), zonedCurrent.toLocalTime().toSecondOfDay());
      }
      if (!(presentInclusive && cmp == 0)) {
        if (future && cmp < 1) {
          throw IllegalArgumentExceptions.requireFuture(fieldName, presentInclusive, obtainTime);
        } else if (!future && cmp > -1) {
          throw IllegalArgumentExceptions.requirePast(fieldName, presentInclusive, obtainTime);
        }
      }
    }
    this.value = Objects.requireNonNull(value);
    this.obtainTime = obtainTime;
  }

  public ZonedDateTime toZonedDateTime() {
    return this.value;
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

  public Instant toInstant() {
    return this.value.toInstant();
  }
}
