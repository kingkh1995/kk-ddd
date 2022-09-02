package com.kk.ddd.support.model.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AccountPageQuery extends AbstractPageQuery {

  private Long id;

  private Long userId;

  private String state;

  private Long[] ids;

  // 日期类默认为字符串，为格式化之后的值
  private String createTimeStart;

  // 日期类默认为字符串，为格式化之后的值
  private String createTimeEnd;
}
