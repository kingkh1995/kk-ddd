package com.kkk.op.support.base;

import com.kkk.op.support.types.LongId;
import com.kkk.op.support.types.PageSize;
import com.kkk.op.support.types.StampedTime;
import com.kkk.op.support.types.Version;
import java.time.Instant;
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

  public StampedTime map2StampedTime(Long l) {
    return StampedTime.from(Instant.ofEpochMilli(l));
  }

  public Long mapFromStampedTime(StampedTime stampedTime) {
    return stampedTime.toInstant().toEpochMilli();
  }
}
