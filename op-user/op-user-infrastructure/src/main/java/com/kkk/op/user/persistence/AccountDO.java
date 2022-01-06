package com.kkk.op.user.persistence;

import java.io.Serializable;
import java.sql.Timestamp;
import lombok.Data;

/**
 * account：用户账户（收付款账户、虚拟账户等）<br>
 * PO类字段和字段类型要求与数据库完全一致 <br>
 * todo... no、type、json等字段待补充
 *
 * @author KaiKoo
 */
@Data
public class AccountDO implements Serializable {

  private Long id;

  private Long userId;

  /** state用来标识可迁移的状态（如state machine），status用来表示不可迁移的状态（如Http status code） */
  private String state;

  // 乐观锁版本号
  private Integer version;

  // mybatis 中 datetime 和 timestamp 类型对应的 jdbcType 均为 Timestamp
  // 数据库中除了 create_time update_time 等其他日期均使用 datetime
  private Timestamp createTime;

  private Timestamp updateTime;
}
