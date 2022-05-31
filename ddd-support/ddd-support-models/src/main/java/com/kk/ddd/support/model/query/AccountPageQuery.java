package com.kk.ddd.support.model.query;

import lombok.Getter;
import lombok.Setter;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Getter
@Setter
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
