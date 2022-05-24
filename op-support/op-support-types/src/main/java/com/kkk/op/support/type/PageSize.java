package com.kkk.op.support.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.kkk.op.support.constant.Constants;
import lombok.EqualsAndHashCode;

/**
 * 分页大小，通过spi方式提供拓展点，可修改默认分页大小和最大分页大小。 <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true)
public class PageSize extends RangedLong {

  // 默认分页大小，并添加缓存
  public static final PageSize DEFAULT = new PageSize(Constants.TYPE.getDefaultPageSize(), null);

  private PageSize(long value, String fieldName) {
    super(value, fieldName, 0L, false, Constants.TYPE.getMaxPageSize(), true);
  }

  /**
   * 内部实现提供私有的静态方法 （如果无特殊处理逻辑可以不提供）<br>
   * 不对外提供构造函数，只提供 valueOf（不可靠输入） 和 of（可靠输入） 静态方法 <br>
   */
  private static PageSize of(long value, String fieldName) {
    return DEFAULT.getValue() == value ? DEFAULT : new PageSize(value, fieldName);
  }

  // 针对可靠输入的 of 方法
  @JsonCreator // 自定义Jackson反序列化，可以用于构造方法和静态工厂方法，使用@JsonProperty注释字段
  public static PageSize of(long l) {
    return of(l, "PageSize");
  }

  // 针对不可靠输入的 valueOf 方法
  public static PageSize valueOf(Object o, String fieldName) {
    return of(parseLong(o, fieldName), fieldName);
  }
}
