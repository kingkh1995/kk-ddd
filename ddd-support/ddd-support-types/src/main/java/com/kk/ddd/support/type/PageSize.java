package com.kk.ddd.support.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kk.ddd.support.constant.Constants;
import com.kk.ddd.support.core.Type;
import com.kk.ddd.support.util.ParseUtils;
import com.kk.ddd.support.util.ValidateUtils;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 分页大小，通过spi方式提供拓展点，可修改默认分页大小和最大分页大小。 <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageSize implements Type, Comparable<PageSize> {

  @Getter @JsonValue private final long value;

  // 默认分页大小，并添加缓存
  public static final PageSize DEFAULT = new PageSize(Constants.TYPE.defaultPageSize());

  private static PageSize of(final long value, final String fieldName) {
    ValidateUtils.minValue(value, 0, false, fieldName);
    ValidateUtils.maxValue(value, Constants.TYPE.maximumPageSize(), true, fieldName);
    if (DEFAULT.getValue() == value) {
      return DEFAULT;
    }
    return new PageSize(value);
  }

  @JsonCreator
  public static PageSize of(final long l) {
    return of(l, "PageSize");
  }

  public static PageSize valueOf(final Object o, final String fieldName) {
    return of(ParseUtils.parseLong(o, fieldName), fieldName);
  }

  @Override
  public int compareTo(PageSize o) {
    return Long.compare(this.getValue(), o.getValue());
  }
}
