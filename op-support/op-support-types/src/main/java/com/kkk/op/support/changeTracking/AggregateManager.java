package com.kkk.op.support.changeTracking;

import com.kkk.op.support.markers.Aggregate;
import com.kkk.op.support.markers.Identifier;
import javax.validation.constraints.NotNull;

/**
 *
 * @author KaiKoo
 */
public interface AggregateManager<T extends Aggregate<ID>, ID extends Identifier> {

    /**
     * 增加一个 Aggregate 的追踪
     */
    void attach(@NotNull T aggregate);

    /**
     * 解除一个 Aggregate 的追踪
     */
    void detach(@NotNull T aggregate);

    /**
     * 更新一个 Aggregate 的追踪
     */
    void merge(@NotNull T aggregate);

    /**
     * 获取一个 Aggregate 的快照
     */
    T find(ID id);

    /**
     * 获取 Aggregate 变更信息
     */
    EntityDiff detectChanges(T aggregate);
}
