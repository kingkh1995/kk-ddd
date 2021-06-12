package com.kkk.op.user.persistence;

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

  // timestamp 和 datetime 均对应 Timestamp
  private Timestamp createTime;

  // todo... 乐观锁及逻辑删除

}
