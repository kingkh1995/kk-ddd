package com.kkk.op.support.changeTracking.diff;

import com.kkk.op.support.base.Entity;
import com.kkk.op.support.marker.Identifiable;
import com.kkk.op.support.marker.Identifier;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * diff核心工具类
 *
 * @author KaiKoo
 */
public final class DiffUtil { // 工具类声明为 final

  // 保证不能被实例化，同时防止反射机制创建对象
  private DiffUtil() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  // 添加缓存，解析属性，过滤@DiffIgnore注解的属性
  private static final Map<Class<?>, SoftReference<Field[]>> FIELD_CACHE =
      new ConcurrentHashMap<>();

  private static Field[] resolve(Class<?> clazz) {
    return Optional.ofNullable(FIELD_CACHE.get(clazz))
        .map(SoftReference::get)
        .orElseGet(
            () -> {
              var fields =
                  Arrays.stream(clazz.getDeclaredFields())
                      .filter(field -> !field.isAnnotationPresent(DiffIgnore.class))
                      .filter(Field::trySetAccessible)
                      .toArray(Field[]::new);
              FIELD_CACHE.put(clazz, new SoftReference<>(fields));
              return fields;
            });
  }

  /**
   * 只保存了 Entity 和 Collection 的对比信息 <br>
   * 1）减少内存消耗 2）详细的对比信息必要性不高 <br>
   *
   * @return 如无对比不同则返回NoneDiff
   */
  public static Diff diff(Entity<?> snapshot, Entity<?> aggregate) {
    // 均无法标识返回Null
    if (unidentified(snapshot) && unidentified(aggregate)) {
      return NoneDiff.INSTANCE;
    }
    final var entityDiff = new EntityDiff(snapshot, aggregate);
    // snapshot无法标识，对比结果为新增
    if (unidentified(snapshot)) {
      return entityDiff.setChangeType(ChangeType.Added);
    }
    // aggregate无法标识，对比结果为移除
    if (unidentified(aggregate)) {
      return entityDiff.setChangeType(ChangeType.Removed);
    }
    // 均可标识，但是标识符不一致，正常情况下不可能发生
    if (!Objects.equals(snapshot.getId(), aggregate.getId())) {
      // unreachable
      throw new ChangeTrackingDiffException("Here should be impossible to reach!");
    }
    // 其实情况下对比结果为修改
    entityDiff.setChangeType(ChangeType.Modified);
    // 类型不一致（同一个类的不同子类），不需对比，直接返回
    if (!Objects.equals(snapshot.getClass(), aggregate.getClass())) {
      return entityDiff;
    }
    // 开始对比
    for (var field : resolve(snapshot.getClass())) {
      try {
        var sObj = field.get(snapshot);
        var aObj = field.get(aggregate);
        // 根据类型做不同的处理
        // instanceof运算符如果obj为null会直接返回false 所以需要使用 ||
        if (sObj instanceof Entity<?> || aObj instanceof Entity<?>) {
          entityDiff.put(field.getName(), diff((Entity<?>) sObj, (Entity<?>) aObj));
        } else if (sObj instanceof Collection<?> || aObj instanceof Collection<?>) {
          entityDiff.put(field.getName(), diff((Collection<?>) sObj, (Collection<?>) aObj));
        } else {
          // 其他类型直接使用equals方法对比（DP和基本数据类型，不应该有Map类型，设计中应该将Map替换为DP或Entity）
          // 不全部对比，只要有一个属性对比不同则停止，也不添加到EntityDiff
          if (!entityDiff.isSelfModified() && !Objects.equals(sObj, aObj)) {
            entityDiff.setSelfModified(true);
          }
        }
      } catch (Exception e) {
        throw new ChangeTrackingDiffException(e);
      }
    }
    // 对比完成，判断对比结果，无变更返回null
    if (entityDiff.isEmpty() && !entityDiff.isSelfModified()) {
      return NoneDiff.INSTANCE;
    }
    return entityDiff;
  }

  /**
   * @param sCol 不能包含空元素
   * @param aCol 不能包含空元素
   * @return 如无对比不同则返回null
   */
  public static Diff diff(Collection<?> sCol, Collection<?> aCol) {
    // 均为空集合表示无修改
    if (isEmpty(sCol) && isEmpty(aCol)) {
      return NoneDiff.INSTANCE;
    }
    final var collectionDiff = new CollectionDiff(sCol, aCol);
    // 开始对比 集合内元素应该全部为 Type 或 Entity，而不应该为Collection或者Map，设计时应被替换为DP或Entity
    // 判断所有元素是否全是Entity（空集合也视作allEntity） 运行时已经擦除了泛型信息，所以无法通过反射获取到实际的类型
    if (allEntity(aCol) && allEntity(sCol)) {
      // 如元素类型全为Entity 根据Id去查找对比
      if (isEmpty(aCol)) {
        // aCol为空则全部为Removed 此时sCol肯定不为空
        sCol.forEach(
            o ->
                collectionDiff.add(
                    new EntityDiff((Entity<?>) o, null).setChangeType(ChangeType.Removed)));
      } else {
        // 将aCol拼装成Map
        var map = new HashMap<Identifier, Entity<?>>(aCol.size(), 1.0f);
        aCol.stream()
            .filter(Objects::nonNull)
            .map(o -> (Entity<?>) o)
            .forEach(
                e -> {
                  if (e.isIdentified()) {
                    // Id重复情况下暂时先不覆盖
                    map.putIfAbsent(e.getId(), e);
                  } else {
                    // 不存在id，添加一个Add类型的EntityDiff
                    collectionDiff.add(new EntityDiff(null, e).setChangeType(ChangeType.Added));
                  }
                });
        // 遍历sCol进行对比，map中元素理论上全部都会被访问到，如果没被访问到则说明该元素不存在快照，这是不应该发生的，直接忽略
        Stream.ofNullable(sCol)
            .flatMap(Collection::stream)
            .map(o -> (Entity<?>) o)
            .forEach(ss -> collectionDiff.add(diff(ss, map.get(ss.getId()))));
      }
      // 对比完成，判断对比结果
      if (collectionDiff.isEmpty()) {
        return NoneDiff.INSTANCE;
      }
    } else if (Objects.equals(sCol, aCol)) {
      // 其他类型直接使用equals方法比较，集合类均有重写equals方法，类似Arrays.deepEquals方法
      return NoneDiff.INSTANCE;
    }
    // 返回结果
    return collectionDiff;
  }

  private static boolean unidentified(Entity<?> entity) {
    return !Identifiable.isIdentified(entity);
  }

  private static boolean isEmpty(Collection<?> collection) {
    return collection == null || collection.isEmpty();
  }

  private static boolean allEntity(Collection<?> collection) {
    // 需要过滤null 否则instanceof会返回false
    return Stream.ofNullable(collection)
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .allMatch(o -> o instanceof Entity);
  }

  public static class ChangeTrackingDiffException extends RuntimeException {

    ChangeTrackingDiffException(Throwable cause) {
      super(cause);
    }

    ChangeTrackingDiffException(String message) {
      super(message);
    }
  }
}
