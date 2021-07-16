package com.kkk.op.support.types;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * <br>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode(callSuper = true)
public class PageSize extends SpecificNumber {

  private static final PageSize DEFAULT_SIZE = new PageSize(TEN, "");

  // todo... 改为可配置，并且是不同项目不同配置
  // 默认最大查询条数
  private static final BigDecimal MAX_SIZE = new BigDecimal(500);

  private PageSize(@NotNull BigDecimal value, String fieldName) {
    super(value, fieldName, ZERO, false, MAX_SIZE, true, 0);
  }

  // 基础方法用于构造对象
  private static PageSize of(@NotNull BigDecimal value, String fieldName) {
    // 使用value.equals()是因为value不能为空
    return value.equals(TEN) ? DEFAULT_SIZE : new PageSize(value, fieldName);
  }

  // 针对可靠输入的 of 方法
  public static PageSize of(long size) {
    return of(new BigDecimal(size), "");
  }

  // 针对不可靠输入的 valueOf 方法
  public static PageSize valueOf(Integer i, String fieldName) {
    return of(parse(i, fieldName), fieldName);
  }

  public static PageSize valueOf(Long l, String fieldName) {
    return of(parse(l, fieldName), fieldName);
  }

  public static PageSize valueOf(String s, String fieldName) {
    return of(parse(s, fieldName), fieldName);
  }
}
