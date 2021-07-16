package com.kkk.op.user.persistence.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.sql.Timestamp;
import lombok.Data;

/**
 * account <br>
 * PO类字段和字段类型要求与数据库完全一致
 *
 * @author KaiKoo
 */
@Data
@TableName("account")
public class AccountDO {

  @TableId(type = IdType.AUTO)
  private Long id;

  private Long userId;

  private String status;

  // mybatis 中 datetime 和 timestimp 类型对应的 jdbcType 均为 Timestamp
  // 数据库中除了 create_time update_time 等其他日期均使用 datetime
  private Timestamp createTime;

  // todo... 乐观锁及逻辑删除

}
