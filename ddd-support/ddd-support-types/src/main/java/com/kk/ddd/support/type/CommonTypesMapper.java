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

  public Long mapFromLongId(LongId longId) {
    return longId.getValue();
  }

  public LongId map2LongId(Long l) {
    return LongId.valueOf(l, "ID");
  }

  public Long mapFromPageSize(PageSize pageSize) {
    return pageSize.getValue();
  }

  public PageSize map2PageSize(Long l) {
    return PageSize.valueOf(l, "分页大小");
  }

  public Integer mapFromVersion(Version version) {
    return version.getValue();
  }

  public Version map2Version(Integer i) {
    return Version.valueOf(i, "版本号");
  }

  public Long mapMillisFromInstant(Instant instant) {
    return instant.toEpochMilli();
  }

  public Instant mapMillis2Instant(Long l) {
    return Instant.ofEpochMilli(l);
  }

  public Date mapDateFromInstant(Instant instant) {
    return Date.from(instant);
  }

  public Instant mapDate2Instant(Date date) {
    return date.toInstant();
  }
}
