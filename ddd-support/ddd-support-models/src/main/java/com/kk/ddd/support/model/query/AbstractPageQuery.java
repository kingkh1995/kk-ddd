package com.kk.ddd.support.model.query;

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
}
