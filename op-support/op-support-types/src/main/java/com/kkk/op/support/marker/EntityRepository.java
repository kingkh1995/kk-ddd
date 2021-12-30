package com.kkk.op.support.marker;

import com.kkk.op.support.base.Entity;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 实体类Repository
 *
 * @author KaiKoo
 */
public interface EntityRepository<T extends Entity<ID>, ID extends Identifier> {

  /** 保存一个 Entity */
  void save(@NotNull T entity);

  /** 将一个 Entity 从 Repository 移除 */
  void remove(@NotNull T entity);

  /** 通过 ID 寻找 Entity。 */
  Optional<T> find(@NotNull ID id);

  /** 通过 IDs 批量寻找 Entity */
  Map<ID, T> find(@NotEmpty Set<ID> ids);
}
