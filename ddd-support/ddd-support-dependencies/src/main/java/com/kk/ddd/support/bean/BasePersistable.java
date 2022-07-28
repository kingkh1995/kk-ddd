package com.kk.ddd.support.bean;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Persistable;

/**
 * 数据库表必须包含id(bigint)、create_time(datetime)、update_time(datetime)。 <br>
 *
 * @author KaiKoo
 */
@MappedSuperclass
@EqualsAndHashCode
public abstract class BasePersistable<PK extends Serializable> implements Persistable<PK> {

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

  public Date getCreateTime() {
    return createTime;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  @Transient
  @Override
  public boolean isNew() {
    return null == getId();
  }
}
