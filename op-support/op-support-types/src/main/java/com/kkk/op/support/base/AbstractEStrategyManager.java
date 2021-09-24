package com.kkk.op.support.base;

import com.kkk.op.support.marker.EStrategy;
import java.lang.reflect.ParameterizedType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 枚举标识策略模式Manager公共基类（使用EnumMap）
 *
 * @author KaiKoo
 */
public abstract class AbstractEStrategyManager<E extends Enum<E>, S extends EStrategy<E>>
    extends AbstractStrategyManager<E, S> {

  private Class<E> eClass;
  private Class<S> sClass;

  {
    // 只有直接子类才初始化，因为子类的子类泛型可能不会相同，会导致报错，无法创建实例。
    if (AbstractEStrategyManager.class.equals(this.getClass().getSuperclass())) {
      var parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
      var actualTypeArguments = parameterizedType.getActualTypeArguments();
      this.eClass = (Class<E>) actualTypeArguments[0];
      this.sClass = (Class<S>) actualTypeArguments[1];
    }
  }

  public AbstractEStrategyManager(Set<CollectTactic> collectTactics) {
    super(collectTactics);
  }

  @Override
  protected Class<S> getSClass() {
    return this.sClass;
  }

  protected Class<E> getEClass() {
    return this.eClass;
  }

  @Override
  protected Supplier<Map<E, S>> getPrimaryMapFactory() {
    return () -> new EnumMap<>(this.getEClass());
  }

  @Override
  protected Supplier<Map<E, List<S>>> getOrderMapFactory() {
    return () -> new EnumMap<>(this.getEClass());
  }

  @Override
  protected Supplier<Map<E, Map<String, S>>> getQualifierMapFactory() {
    return () -> new EnumMap<>(this.getEClass());
  }
}
