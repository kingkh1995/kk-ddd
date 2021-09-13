package com.kkk.op.support.model.query;

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
}
