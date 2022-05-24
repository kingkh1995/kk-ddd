package com.kkk.op.user.persistence;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * account：用户账户（收付款账户、虚拟账户等）<br>
 * PO类字段和字段类型要求与数据库完全一致，且应该是record。 <br>
 * 根据阿里巴巴Java开发手册，日期类型应该使用DATETIME和java.util.Date。
 *
 * @author KaiKoo
 */
@Data
public class AccountPO implements Serializable {

  private Long id;

  private Date createTime;

  private Date updateTime;

  private Long userId;

  /** state用来标识可迁移的状态（如state machine），status用来表示不可迁移的状态（如Http status code） */
  private String state;

  // 乐观锁版本号
  private Integer version;
}
