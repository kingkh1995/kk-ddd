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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 策略模式Manager公共基类
 *
 * @author KaiKoo
 */
public abstract class AbstractStrategyManager<T extends Enum<T>, S extends Strategy<T>>
    extends ApplicationContextAwareBean {

  /**
   * 政策实现类收集方案 <br>
   * 均使用EnumMap收集
   */
  protected enum CollectTactic {
    /** 只收集单个实现，优先收集@Primary修饰的类 */
    PRIMARY,
    /** 全部收集为list，并按@Order注解的值排列 */
    ORDER,
    /** 全部收集为map，key为@Qualifier注解的值 */
    QUALIFIER;
  }

  private final Set<CollectTactic> collectTactics;
  private final Class<T> tClass;
  private final Class<S> sClass;
  private Collection<S> strategys;
  private Map<T, S> primaryStrategyMap;
  private Map<T, List<S>> orderStrategyMap;
  private Map<T, Map<String, S>> qualifierStrategyMap;

  protected AbstractStrategyManager(@NotEmpty Set<CollectTactic> collectTactics) {
    if (collectTactics == null || collectTactics.isEmpty()) {
      throw new NullPointerException("collectTactics is empty!");
    }
    this.collectTactics = collectTactics;
  }

  {
    var actualTypeArguments =
        ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
    this.tClass = (Class<T>) actualTypeArguments[0];
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
      this.primaryStrategyMap =
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
      this.orderStrategyMap =
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
    // qualifier 先分组收集为list再收集为UnmodifiableMap
    if (this.collectTactics.contains(CollectTactic.QUALIFIER)) {
      this.qualifierStrategyMap =
          this.strategys.stream()
              .collect(
                  Collectors.groupingBy(
                      Strategy::getStrategyID,
                      () -> new EnumMap<>(this.tClass),
                      Collectors.collectingAndThen(
                          Collectors.toList(),
                          ss ->
                              ss.stream()
                                  .collect(
                                      Collectors.toUnmodifiableMap(
                                          s ->
                                              Optional.ofNullable(
                                                      s.getClass().getAnnotation(Qualifier.class))
                                                  .map(Qualifier::value)
                                                  .orElse("default"),
                                          Function.identity())))));
    }
  }

  protected S getSingleton(T t) {
    return Objects.requireNonNull(this.primaryStrategyMap).get(t);
  }

  protected List<S> getList(T t) {
    return Objects.requireNonNull(this.orderStrategyMap).get(t);
  }

  protected Map<String, S> getMap(T t) {
    return Objects.requireNonNull(this.qualifierStrategyMap).get(t);
  }
}
