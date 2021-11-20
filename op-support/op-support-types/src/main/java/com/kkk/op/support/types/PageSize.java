package com.kkk.op.support.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Objects;
import lombok.EqualsAndHashCode;

/**
 * <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true)
public class PageSize extends RangedLong {

  // todo... 以下改为可配置，并且是不同项目不同配置

  // 默认分页大小，并添加缓存
  public static final PageSize DEFAULT = new PageSize(10L, null);

  // 默认最大查询条数
  private static final long MAX_SIZE = 500L;

  private PageSize(long value, String fieldName) {
    super(value, fieldName, 0L, false, MAX_SIZE, true);
  }

  /**
   * 内部实现提供私有的 of 静态方法 （如果无特殊处理逻辑可以不提供）<br>
   * 不对外提供构造函数，只提供 valueOf（不可靠输入） 和 from（可靠输入） 静态方法 <br>
   */
  private static PageSize of(long value, String fieldName) {
    return Objects.equals(DEFAULT.getValue(), value) ? DEFAULT : new PageSize(value, fieldName);
  }

  // 针对可靠输入的 from 方法
  @JsonCreator // 自定义Jackson反序列化，可以用于构造方法和静态工厂方法，使用@JsonProperty注释字段
  public static PageSize from(long l) {
    return of(l, "PageSize");
  }

  // 针对不可靠输入的 valueOf 方法
  public static PageSize valueOf(Long l, String fieldName) {
    return of(parseLong(l, fieldName), fieldName);
  }
}
