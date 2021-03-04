package com.kkk.op.support.changeTracking;

import com.kkk.op.support.changeTracking.diff.EntityDiff;
import com.kkk.op.support.markers.Aggregate;
import com.kkk.op.support.markers.Identifier;
import javax.validation.constraints.NotNull;

/**
 * 聚合根追踪manager接口
 * **DO类需要加上乐观锁，防止bug发生*
 * @author KaiKoo
 */
public interface AggregateTrackingManager<T extends Aggregate<ID>, ID extends Identifier> {

    /**
     * 增加一个 Aggregate 的追踪（如果已存在追踪，do nothing，相当于保证了可重复读）
     */
    void attach(@NotNull T aggregate);

    /**
     * 解除一个 Aggregate 的追踪（在delete操作完成之后再去更新）
     */
    void detach(@NotNull T aggregate);

    /**
     * 更新一个 Aggregate 的追踪（在update操作完成之后再去更新）
     */
    void merge(@NotNull T aggregate);

    /**
     * 获取一个 Aggregate 的快照
     */
    T find(@NotNull ID id);

    /**
     * 获取 Aggregate 变更信息
     */
    EntityDiff detectChanges(@NotNull T aggregate);
}
