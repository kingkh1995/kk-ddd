package com.kk.ddd.support.model.dto;

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
   * @see com.kk.ddd.support.enums.AccountTypeEnum
   */
  private String type;

  private String principal;

  private Long unbindTimestamp;

  private Long createTimestamp;

  private Long updateTimestamp;

  private Integer version;
}
