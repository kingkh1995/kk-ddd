package com.kkk.op.support.marker;

import com.kkk.op.support.base.Entity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * domain service 参数使用 @NotNull @NotEmpty 标识 <br>
 * todo... query service
 *
 * @author KaiKoo
 */
public interface EntityService<T extends Entity<ID>, ID extends Identifier> {

  void save(@NotNull T t);

  void remove(@NotNull T t);

  Optional<T> find(@NotNull ID id);

  List<T> find(@NotEmpty Set<ID> ids);
}
