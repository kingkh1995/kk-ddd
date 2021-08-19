package com.kkk.op.support.base;

import com.kkk.op.support.marker.Strategy;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.constraints.NotEmpty;
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
public abstract class AbstractStrategyManager<E extends Enum<E>, S extends Strategy<E>>
    extends ApplicationContextAwareBean {

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
    QUALIFIER;
  }

  private final Set<CollectTactic> collectTactics;
  private final Class<E> tClass;
  private final Class<S> sClass;
  private Collection<S> strategys;
  private Map<E, S> primaryMap;
  private Map<E, List<S>> orderMap;
  private Map<E, Map<String, S>> qualifierMap;

  public AbstractStrategyManager(@NotEmpty Set<CollectTactic> collectTactics) {
    if (collectTactics == null || collectTactics.isEmpty()) {
      throw new NullPointerException("collectTactics is empty!");
    }
    this.collectTactics = collectTactics;
  }

  {
    var actualTypeArguments =
        ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
    this.tClass = (Class<E>) actualTypeArguments[0];
    this.sClass = (Class<S>) actualTypeArguments[1];
  }

  @Override
  public void afterPropertiesSet() {
    if (strategys != null) {
      return;
    }
    this.strategys = this.getApplicationContext().getBeansOfType(this.sClass).values();
    collecting();
  }

  private void collecting() {
    // primary
    if (this.collectTactics.contains(CollectTactic.PRIMARY)) {
      this.primaryMap =
          this.strategys.stream()
              .collect(
                  Collectors.toMap(
                      Strategy::getStrategyID,
                      Function.identity(),
                      (s, s2) -> s2.getClass().getAnnotation(Primary.class) == null ? s : s2,
                      () -> new EnumMap<>(this.tClass)));
    }
    // order 先分组收集为list再排序并转换为UnmodifiableList
    if (this.collectTactics.contains(CollectTactic.ORDER)) {
      this.orderMap =
          this.strategys.stream()
              .collect(
                  Collectors.groupingBy(
                      Strategy::getStrategyID,
                      () -> new EnumMap<>(this.tClass),
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
                                  .collect(Collectors.toUnmodifiableList()))));
    }
    // 先分组，再将子组收集为UnmodifiableMap
    if (this.collectTactics.contains(CollectTactic.QUALIFIER)) {
      this.qualifierMap =
          this.strategys.stream()
              .collect(
                  Collectors.groupingBy(
                      Strategy::getStrategyID,
                      () -> new EnumMap<>(this.tClass),
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
  protected S getSingleton(@NotNull E e) {
    return Objects.requireNonNull(this.primaryMap).get(e);
  }

  protected List<S> getList(@NotNull E e) {
    return Objects.requireNonNull(this.orderMap).get(e);
  }

  protected Map<String, S> getMap(@NotNull E e) {
    return Objects.requireNonNull(this.qualifierMap).get(e);
  }
}
