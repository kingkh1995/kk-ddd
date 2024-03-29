package com.kk.ddd.job.domain;

import com.kk.ddd.support.bean.BasePersistable;
import com.kk.ddd.support.constant.JobStateEnum;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 任务 <br>
 *
 * @author KaiKoo
 */
@Data
@EqualsAndHashCode(callSuper = true)
@DynamicInsert // 动态插入忽略为null的字段
@DynamicUpdate // 动态更新忽略值未变化的字段
@Entity // 标明为实体类
@Table(
    name = "job",
    indexes = @Index(name = "idx_state_topic", columnList = "state, topic")) // 指定表名和创建索引
public class JobDO extends BasePersistable<Long> {

  @Id // 标明为主键
  @GeneratedValue(strategy = GenerationType.IDENTITY) // 表明主键生成策略为auto_increment
  private Long id;

  /**
   * 任务状态
   *
   * @see JobStateEnum
   */
  @Enumerated(EnumType.STRING) // 映射为枚举，并使用字符形式映射。
  @Column(name = "state", columnDefinition = "CHAR(1) NOT NULL")
  private JobStateEnum state;

  /** 任务消费的topic。 */
  @Column(name = "topic", length = 50, nullable = false, updatable = false)
  private String topic;

  /** 任务应执行时间 */
  @Column(name = "action_time", columnDefinition = "DATETIME(3) NOT NULL", updatable = false)
  private Date actionTime;

  /** 上下文信息，json格式，自行解析。 */
  @Column(name = "context", columnDefinition = "JSON NULL", updatable = false)
  private String context;
}
