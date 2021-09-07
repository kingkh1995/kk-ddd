package com.kkk.op.support.types;

import com.fasterxml.jackson.annotation.JsonCreator;
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

  // 对默认分页大小10条添加缓存
  public static final PageSize DEFAULT_SIZE = new PageSize(TEN);

  // todo... 改为可配置，并且是不同项目不同配置
  // 默认最大查询条数
  private static final BigDecimal MAX_SIZE = new BigDecimal(500);

  private PageSize(@NotNull BigDecimal value) {
    super(value, "PageSize", ZERO, false, MAX_SIZE, true, 0);
  }

  // 私有的基础 of 静态方法
  private static PageSize of(@NotNull BigDecimal size) {
    // 不判空因为value不能为空
    return size.equals(DEFAULT_SIZE.value())
        ? DEFAULT_SIZE
        : new PageSize(new BigDecimal(size.toString()));
  }

  // 针对可靠输入的 from 方法
  @JsonCreator
  public static PageSize from(@NotNull Number size) {
    return of(new BigDecimal(size.toString()));
  }

  // 针对不可靠输入的 valueOf 方法
  public static PageSize valueOf(Integer i) {
    return of(parse(i, "分页大小"));
  }

  public static PageSize valueOf(Long l) {
    return of(parse(l, "分页大小"));
  }

  public static PageSize valueOf(String s) {
    return of(parse(s, "分页大小"));
  }
}
