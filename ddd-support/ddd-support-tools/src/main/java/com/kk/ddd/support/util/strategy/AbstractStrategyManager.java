package com.kk.ddd.support.util.strategy;

import com.kk.ddd.support.util.ApplicationContextAwareSingleton;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 策略模式Manager公共基类 <br>
 * （使用工厂方法模式：工厂类定义创造不同类型产品的接口，由子类去决定实例化的产品类型。）
 *
 * @author KaiKoo
 */
public abstract class AbstractStrategyManager<K, S extends Strategy<K>>
    extends ApplicationContextAwareSingleton {

  /**
   * 政策实现类收集方案 <br>
   * 均使用EnumMap收集
   */
  protected enum CollectTactic {
    /** 只收集单个实现，优先收集@Primary注解的实现 */
    PRIMARY,
    /**
     * 全部收集为list，并按@Order注解的值排列 <br>
     * 可以用来实现管道模式
     */
    ORDER,
    /** 全部收集为map，key为@Qualifier注解的值，无注解则默认为default */
    QUALIFIER
  }

  private final Set<CollectTactic> collectTactics;
  private Collection<S> strategies;
  private Map<K, S> primaryMap;
  private Map<K, List<S>> orderMap;
  private Map<K, Map<String, S>> qualifierMap;

  public AbstractStrategyManager(Set<CollectTactic> collectTactics) {
    this.collectTactics = Optional.ofNullable(collectTactics).orElse(Collections.emptySet());
  }

  protected abstract Class<S> getSClass();

  protected Supplier<Map<K, S>> getPrimaryMapFactory() {
    return HashMap::new;
  }

  protected Supplier<Map<K, List<S>>> getOrderMapFactory() {
    return HashMap::new;
  }

  protected Supplier<Map<K, Map<String, S>>> getQualifierMapFactory() {
    return HashMap::new;
  }

  @Override
  public void afterSingletonsInstantiated() {
    if (this.strategies != null) {
      return;
    }
    this.strategies =
        Collections.unmodifiableCollection(
            this.getApplicationContext().getBeansOfType(this.getSClass()).values());
    collecting();
  }

  private void collecting() {
    // primary
    if (this.collectTactics.contains(CollectTactic.PRIMARY)) {
      this.primaryMap =
          this.strategies.stream()
              .collect(
                  Collectors.toMap(
                      Strategy::getIdentifier,
                      Function.identity(),
                      (s, s2) -> s2.getClass().getAnnotation(Primary.class) == null ? s : s2,
                      this.getPrimaryMapFactory()));
    }
    // order 先分组收集为list再排序并转换为UnmodifiableList
    if (this.collectTactics.contains(CollectTactic.ORDER)) {
      this.orderMap =
          this.strategies.stream()
              .collect(
                  Collectors.groupingBy(
                      Strategy::getIdentifier,
                      this.getOrderMapFactory(),
                      Collectors.collectingAndThen(
                          Collectors.toList(),
                          ss ->
                              ss.stream()
                                  .sorted(
                                      Comparator.comparingInt(
                                          s ->
                                              Optional.ofNullable(
                                                      s.getClass().getAnnotation(Order.class))
                                                  .map(Order::value)
                                                  .orElse(Ordered.LOWEST_PRECEDENCE)))
                                  .toList())));
    }
    // qualifier 先分组，再将子组收集为UnmodifiableMap
    if (this.collectTactics.contains(CollectTactic.QUALIFIER)) {
      this.qualifierMap =
          this.strategies.stream()
              .collect(
                  Collectors.groupingBy(
                      Strategy::getIdentifier,
                      this.getQualifierMapFactory(),
                      Collectors.toUnmodifiableMap(
                          s ->
                              Optional.ofNullable(s.getClass().getAnnotation(Qualifier.class))
                                  .map(Qualifier::value)
                                  .orElse("default"),
                          Function.identity())));
    }
  }

  /**
   * 根据策略枚举值获取收集到的策略实现类 <br>
   * 以下方法均不能返回空，因为从业务角度必须存在对应的策略实现类。
   */
  protected final Collection<S> getAll() {
    return this.strategies;
  }

  protected final S getSingleton(@NotNull K k) {
    return Objects.requireNonNull(this.primaryMap).get(k);
  }

  protected final List<S> getList(@NotNull K k) {
    return Objects.requireNonNull(this.orderMap).get(k);
  }

  protected final Map<String, S> getMap(@NotNull K k) {
    return Objects.requireNonNull(this.qualifierMap).get(k);
  }
}
