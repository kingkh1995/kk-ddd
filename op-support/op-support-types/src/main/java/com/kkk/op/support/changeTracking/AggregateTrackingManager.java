package com.kkk.op.support.changeTracking;

import com.kkk.op.support.changeTracking.diff.EntityDiff;
import com.kkk.op.support.marker.Aggregate;
import com.kkk.op.support.marker.Identifier;
import javax.validation.constraints.NotNull;

/**
 * 聚合根追踪manager接口
 * 建议DO类都加上乐观锁，防止并发冲突
 * @author KaiKoo
 */
public interface AggregateTrackingManager<T extends Aggregate<ID>, ID extends Identifier> {

    /**
     * 增加一个 Aggregate 的追踪（如果已被追踪，不更新追踪，相当于保证了可重复读）
     */
    void attach(@NotNull T aggregate);

    /**
     * 解除一个 Aggregate 的追踪（在delete操作完成之后再去解除）
     */
    void detach(@NotNull T aggregate);

    /**
     * 更新一个 Aggregate 的追踪（在update操作完成之后再去更新）
     */
    void merge(@NotNull T aggregate);

    /**
     * 获取 Aggregate 变更信息
     */
    EntityDiff detectChanges(@NotNull T aggregate);

    /**
     * 判断 Aggregate 的快照是否存在
     */
    boolean exist(@NotNull ID id);

    /**
     * 获取一个 Aggregate 的快照 （安全副本）
     */
    T find(@NotNull ID id);

}
