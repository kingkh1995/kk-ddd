package com.kk.ddd.support.type;

import java.time.Instant;
import java.util.Date;
import org.springframework.stereotype.Component;

/**
 * 公共DP类通用 mapstruct mapper<br>
 *
 * @author KaiKoo
 */
@Component
public class CommonTypesMapper {

  public Long mapFromLongId(final LongId longId) {
    return longId.getValue();
  }

  public LongId map2LongId(final Long l) {
    return LongId.valueOf(l, "ID");
  }

  public Long mapFromPageSize(final PageSize pageSize) {
    return pageSize.getValue();
  }

  public PageSize map2PageSize(final Long l) {
    return PageSize.valueOf(l, "分页大小");
  }

  public Integer mapFromVersion(final Version version) {
    return version.getValue();
  }

  public Version map2Version(final Integer i) {
    return Version.valueOf(i, "版本号");
  }

  public Long mapMillisFromInstant(final Instant instant) {
    return instant.toEpochMilli();
  }

  public Instant mapMillis2Instant(final Long l) {
    return Instant.ofEpochMilli(l);
  }

  public Date mapDateFromInstant(final Instant instant) {
    return Date.from(instant);
  }

  public Instant mapDate2Instant(final Date date) {
    return date.toInstant();
  }
}
