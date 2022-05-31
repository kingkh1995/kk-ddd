package com.kk.ddd.support.util.strategy;

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
    // 初始化代码是在具体的子类中执行，如果是子类的子类，则可能修改了泛型，会导致报错，故只有直接子类才调用。
    if (AbstractEStrategyManager.class.equals(this.getClass().getSuperclass())) {
      var actualTypeArguments =
          ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
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
