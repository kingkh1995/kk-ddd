package com.kk.ddd.support.marker;

import com.kk.ddd.support.base.Entity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * CQRS架构中领域服务之查询服务 <br>
 *
 * @author KaiKoo
 */
public interface QueryService<T extends Entity<ID>, ID extends Identifier> {

  Optional<T> find(@NotNull ID id);

  List<T> find(@NotEmpty Set<ID> ids);
}
