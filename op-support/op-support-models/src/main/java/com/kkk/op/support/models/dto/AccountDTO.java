package com.kkk.op.support.models.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Data
public class AccountDTO implements Serializable {

  private Long id;

  private Long userId;

  /**
   * 不使用枚举值，因为无法向前兼容（新增枚举值，如果使用方未更新依赖，会发生转换异常）
   *
   * @see com.kkk.op.support.enums.AccountStatusEnum
   */
  private String status;

  /** 日期类默认转换为毫秒时间戳 */
  private Long createTime;
}
