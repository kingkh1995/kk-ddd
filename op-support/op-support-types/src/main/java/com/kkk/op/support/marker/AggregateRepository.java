package com.kkk.op.support.marker;

import com.kkk.op.support.base.Aggregate;
import javax.validation.constraints.NotNull;

/**
 * 聚合根类Repository
 * @author KaiKoo
 */
public interface AggregateRepository<T extends Aggregate<ID>, ID extends Identifier> extends EntityRepository<T, ID> {

    /**
     * 将一个 Aggregate 附属到一个 Repository，让它变为可追踪。
     */
    void attach(@NotNull T aggregate);

    /**
     * 解除一个 Aggregate 的追踪
     */
    void detach(@NotNull T aggregate);

}
