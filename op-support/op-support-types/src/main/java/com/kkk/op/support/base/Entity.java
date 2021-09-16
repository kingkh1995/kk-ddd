package com.kkk.op.support.base;

import com.kkk.op.support.marker.Identifiable;
import com.kkk.op.support.marker.Identifier;

/**
 * Entity：拥有唯一标识和业务行为，尽可能的由DP组成 <br>
 * 实体类 marker
 *
 * @author KaiKoo
 */
public abstract class Entity<ID extends Identifier> implements Identifiable<ID> {

  /** 验证该实体类参数是否合法 */
  public abstract void validate();

  /** 设置id */
  protected abstract void setId(ID id);

  @Override
  public void fillInId(ID id) {
    if (this.isIdentified()) {
      return;
    }
    if (id == null) {
      throw new IllegalArgumentException("can't fill in id with null");
    }
    this.setId(id);
  }
}
