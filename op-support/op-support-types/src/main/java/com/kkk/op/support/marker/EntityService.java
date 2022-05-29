package com.kkk.op.support.marker;

import com.kkk.op.support.base.Entity;
import javax.validation.constraints.NotNull;

/**
 * CQRS架构中领域服务之命令服务 <br>
 * CQRS: 命令查询责任分离，命令(写操作)和查询(读操作)使用不同的数据模型，通过领域事件将命令模型中的变更传播到查询模型中。 <br>
 * domain service 参数使用 @NotNull @NotEmpty 标识
 *
 * @author KaiKoo
 */
public interface EntityService<T extends Entity<ID>, ID extends Identifier> {

  void save(@NotNull T t);

  void remove(@NotNull T t);
}
