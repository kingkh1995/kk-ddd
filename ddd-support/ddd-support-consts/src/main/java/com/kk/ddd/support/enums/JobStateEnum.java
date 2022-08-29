package com.kk.ddd.support.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Getter
@RequiredArgsConstructor
public enum JobStateEnum {
  P("pending"),
  A("actioned"),
  D("dead");

  private final String desc;
}
