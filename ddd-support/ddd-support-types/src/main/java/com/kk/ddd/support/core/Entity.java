package com.kk.ddd.support.core;

import com.kk.ddd.support.util.NameGenerator;
import javax.validation.constraints.NotBlank;

/**
 * 实体类 marker <br>
 * Entity：拥有唯一标识和业务行为，尽可能的由DP组成。 <br>
 * 为充血模型，处理持久层的操作交由Repository，其他业务逻辑都应该全部位于领域和领域服务中。<br>
 * 即Entity在调用所有方法时（除了创建）都应该是合法的，如果需要更新领域，则调用save方法或者发送领域事件（CQRS）。
 *
 * @author KaiKoo
 */
public abstract class Entity<ID extends Identifier> implements Identifiable<ID> {

  /** 生成锁名 */
  public @NotBlank String generateLockName(NameGenerator lockNameGenerator) {
    throw new UnsupportedOperationException();
  }

  /** 设置id */
  protected abstract void setId(ID id);

  @Override
  public void fillInId(ID id) {
    if (this.isIdentified()) {
      throw new IllegalArgumentException("Already identified!");
    }
    if (id == null) {
      throw new IllegalArgumentException("Can't fill in with null");
    }
    this.setId(id);
  }
}
