package com.kkk.op.support.types;

import java.math.BigDecimal;
import lombok.EqualsAndHashCode;

/**
 * <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true)
public class PageSize extends SpecificNumber {

  private static final transient PageSize DEFAULT = of(TEN);

  // todo... 改为可配置，并且是不同项目不同配置
  // 默认最大查询条数
  private static final transient BigDecimal MAX_SIZE = new BigDecimal("1000");

  private PageSize(BigDecimal value, String fieldName) {
    super(value, fieldName, ZERO, false, null, null, 0);
  }

  /** 不对外提供构造函数，只提供 valueOf 静态方法 */
  public static PageSize valueOf(Integer i, String fieldName) {
    return new PageSize(parse(i, fieldName), fieldName);
  }

  public static PageSize valueOf(Long l, String fieldName) {
    return new PageSize(parse(l, fieldName), fieldName);
  }

  public static PageSize valueOf(String s, String fieldName) {
    return new PageSize(parse(s, fieldName), fieldName);
  }

  public static PageSize of(long size) {
    return new PageSize(new BigDecimal(size), "");
  }

  public static PageSize of(BigDecimal size) {
    return new PageSize(size, "");
  }
}
