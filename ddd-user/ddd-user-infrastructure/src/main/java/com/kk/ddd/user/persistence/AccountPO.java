package com.kk.ddd.user.persistence;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * account <br>
 *
 * @author KaiKoo
 */
@Data
public class AccountPO implements Serializable {

  private Long id;

  /**
   * 用户ID <br/>
   * 聚合根主键
   */
  private Long userId;

  /**
   * 账户类型 <br/>
   * @see com.kk.ddd.support.enums.AccountTypeEnum
   */
  private String type;

  /**
   * 账户标识（账号）
   */
  private String principal;

  /**
   * 解绑时间
   */
  private Date unbindTime;

  private Date createTime;

  private Date updateTime;

  private Integer version;
}
