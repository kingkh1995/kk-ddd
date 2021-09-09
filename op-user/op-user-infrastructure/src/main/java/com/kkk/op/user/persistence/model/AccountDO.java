package com.kkk.op.user.persistence.model;

import java.sql.Timestamp;
import lombok.Data;

/**
 * account <br>
 * PO类字段和字段类型要求与数据库完全一致
 *
 * @author KaiKoo
 */
@Data
public class AccountDO {

  private Long id;

  private Long userId;

  /** state用来标识可迁移的状态（如state machine），status用来表示不可迁移的状态（如Http status code） */
  private String state;

  // mybatis 中 datetime 和 timestimp 类型对应的 jdbcType 均为 Timestamp
  // 数据库中除了 create_time update_time 等其他日期均使用 datetime
  private Timestamp createTime;

  // 乐观锁版本号
  private Integer version;
}
