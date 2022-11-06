package com.kk.ddd.support.model.dto;

import com.kk.ddd.support.constant.UserStateEnum;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Data
public class UserDTO implements Serializable {

  private Long id;

  /**
   * 不直接使用枚举类型，因为无法向前兼容（新增枚举值，如果使用方未更新依赖，会发生转换异常）
   *
   * @see UserStateEnum
   */
  private String state;

  private String name;

  private String phone;

  private String email;

  /** 日期类默认转换为毫秒时间戳 */
  private Long createTimestamp;

  private Long updateTimestamp;

  private Integer version;

  private List<AccountDTO> accounts;
}
