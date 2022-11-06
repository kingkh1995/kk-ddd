package com.kk.ddd.support.model.dto;

import com.kk.ddd.support.constant.AccountTypeEnum;
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
   * @see AccountTypeEnum
   */
  private String type;

  private String principal;

  private Long unbindTimestamp;

  private Long createTimestamp;

  private Long updateTimestamp;

  private Integer version;
}
