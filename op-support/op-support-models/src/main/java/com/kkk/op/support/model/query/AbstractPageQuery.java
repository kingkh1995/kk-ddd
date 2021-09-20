package com.kkk.op.support.model.query;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * 查询类DTO基类（默认包含分页参数）
 *
 * @author KaiKoo
 */
@Getter
@Setter
public abstract class AbstractPageQuery implements Serializable {

  protected Long size = 10L;

  protected Long current = 1L;

  // 日期类默认为字符串，为格式化之后的值
  private String createTimeStart;

  // 日期类默认为字符串，为格式化之后的值
  private String createTimeEnd;
}
