package com.kk.ddd.job.domain;

import com.kk.ddd.support.bean.BasePersistable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;

/**
 * 本地事务执行状态表 <br>
 *
 * @author KaiKoo
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "local_tx")
public class LocalTxDO extends BasePersistable<String> {

  @Id
  @Column(name = "id", columnDefinition = "CHAR(36) NOT NULL") // UUID
  private String id;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "state", columnDefinition = "TINYINT(4) UNSIGNED NOT NULL")
  private RocketMQLocalTransactionState state;
}
