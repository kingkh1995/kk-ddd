package com.kkk.op.support.bean;

import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.changeTracking.AbstractAggregateTrackingManager;
import com.kkk.op.support.changeTracking.AggregateTrackingContext;
import com.kkk.op.support.marker.Identifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

/**
 * 使用ThreadLocal防止多个线程公用一份快照 <br>
 * 内存泄漏解决方案： <br>
 * 1.在初始化ThreadLocal时将其记录到Recorder；<br>
 * 2.在拦截器的afterCompletion方法中移除所有的ThreadLocal。<br>
 *
 * @author KaiKoo
 */
@Builder
public class ThreadLocalAggregateTrackingManager<T extends Aggregate<ID>, ID extends Identifier>
    extends AbstractAggregateTrackingManager<T, ID> {

  private final ThreadLocal<AggregateTrackingContext<T, ID>> holder =
      ThreadLocal.withInitial(
          () -> {
            // Lambda表达式中的this指向外部实例，而匿名类中的this指向匿名类实例。
            ThreadLocalRecorder.record(this.holder);
            return new MapAggregateTrackingContext<>();
          });

  @Getter private final UnaryOperator<T> snapshooter;

  private ThreadLocalAggregateTrackingManager(final UnaryOperator<T> snapshooter) {
    this.snapshooter = Objects.requireNonNull(snapshooter);
  }

  @Override
  public T snapshoot(T aggregate) {
    return this.snapshooter.apply(aggregate);
  }

  @Override
  protected AggregateTrackingContext<T, ID> getContext() {
    return holder.get();
  }

  protected static class MapAggregateTrackingContext<T extends Aggregate<ID>, ID extends Identifier>
      implements AggregateTrackingContext<T, ID> {

    private final Map<ID, T> map = new HashMap<>();

    @Override
    public boolean contains(@NotNull ID id) {
      return this.map.containsKey(id);
    }

    @Override
    public T remove(@NotNull ID id) {
      return this.map.remove(id);
    }

    @Override
    public void put(@NotNull T t) {
      if (t.isIdentified()) {
        this.map.put(t.getId(), t);
      }
    }

    @Override
    public T get(@NotNull ID id) {
      return this.map.get(id);
    }
  }
}
