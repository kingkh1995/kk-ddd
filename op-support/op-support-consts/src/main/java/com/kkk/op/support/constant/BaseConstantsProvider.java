package com.kkk.op.support.constant;

/**
 * <br>
 *
 * @author KaiKoo
 */
public interface BaseConstantsProvider {

  default String getDefaultSucceedCode() {
    return "0";
  }

  default String getDefaultSucceedMessage() {
    return "ok";
  }

  default String getDefaultFailCode() {
    return "1";
  }
}
