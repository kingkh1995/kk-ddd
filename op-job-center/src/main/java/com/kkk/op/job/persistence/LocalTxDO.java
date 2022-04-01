package com.kkk.op.job.persistence;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;

/**
 * 本地事务执行状态表 <br>
 *
 * @author KaiKoo
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "local_tx")
public class LocalTxDO implements Serializable {

  @Id private String txId;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "state", columnDefinition = "TINYINT(4) UNSIGNED NOT NULL")
  private RocketMQLocalTransactionState state;

  @Column(
      name = "create_time",
      columnDefinition = "DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP",
      insertable = false,
      updatable = false)
  private Date createTime;

  @Column(
      name = "update_time",
      columnDefinition =
          "DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",
      insertable = false,
      updatable = false)
  private Date updateTime;
}
