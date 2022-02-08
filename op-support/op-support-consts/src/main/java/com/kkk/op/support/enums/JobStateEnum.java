package com.kkk.op.support.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Getter
@AllArgsConstructor
public enum JobStateEnum {

  P("pending"),
  A("actioned"),
  D("dead");

  private final String desc;
}
