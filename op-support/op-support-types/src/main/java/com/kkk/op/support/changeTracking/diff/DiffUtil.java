package com.kkk.op.support.changeTracking.diff;

import com.kkk.op.support.base.Entity;
import com.kkk.op.support.marker.Identifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/** @author KaiKoo */
public final class DiffUtil { // 工具类声明为 final

  // 保证不能被实例化，同时防止反射机制创建对象
  private DiffUtil() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  /**
   * 只保存了 Entity 和 Collection 的对比信息 <br>
   * 1）减少内存消耗 2）详细的对比信息必要性不高 <br>
   * todo... 增加参数，支持ValueDiff
   *
   * @return 如无对比不同则返回null
   */
  public static <T extends Entity<?>> EntityDiff diff(T snapshot, T aggregate) {
    // 均无法标识返回Null
    if (isNullOrUnidentified(snapshot) && isNullOrUnidentified(aggregate)) {
      return null;
    }
    final var entityDiff = new EntityDiff(snapshot, aggregate);
    // snapshot无法标识，对比结果为新增
    if (isNullOrUnidentified(snapshot)) {
      entityDiff.setChangeType(ChangeType.Added);
      return entityDiff;
    }
    // aggregate无法标识，对比结果为移除
    if (isNullOrUnidentified(aggregate)) {
      entityDiff.setChangeType(ChangeType.Removed);
      return entityDiff;
    }
    // 均可标识，但是标识符不一致，正常情况下不可能发生
    if (!Objects.equals(snapshot.getId(), aggregate.getId())) {
      // unreachable
      return null;
    }
    // 其实情况下对比结果为修改
    entityDiff.setChangeType(ChangeType.Modified);
    // 类型不一致（同一个类的不同子类），不需对比，直接返回
    if (!Objects.equals(snapshot.getClass(), aggregate.getClass())) {
      return entityDiff;
    }
    // 开始对比
    // todo... 反射信息添加缓存
    var fields = snapshot.getClass().getDeclaredFields();
    for (var field : fields) {
      field.trySetAccessible();
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
      return null;
    }
    return entityDiff;
  }

  /**
   * @param sCol 不能包含空元素
   * @param aCol 不能包含空元素
   * @return 如无对比不同则返回null
   */
  public static CollectionDiff diff(Collection<?> sCol, Collection<?> aCol) {
    // 均为空集合表示无修改
    if (isNullOrEmpty(sCol) && isNullOrEmpty(aCol)) {
      return null;
    }
    final var collectionDiff = new CollectionDiff(sCol, aCol);
    // 开始对比 集合内元素应该全部为 Type 或 Entity，而不应该为Collection或者Map，设计时应被替换为DP或Entity
    // 判断所有元素是否全是Entity并过滤空元素 运行时已经擦除了泛型信息，所以无法通过反射获取到实际的类型
    if (allEntity(aCol) && allEntity(sCol)) {
      // 如元素类型为Entity 根据Id去查找匹配
      // 拼装sMap
      var sMap = new HashMap<Identifier, Entity<?>>(sCol.size());
      Optional.ofNullable(sCol)
          .ifPresent(
              col ->
                  col.stream()
                      .filter(Objects::nonNull)
                      .forEach(t -> put2IdEntityMap(sMap, (Entity<?>) t)));
      // 拼装aMap
      var aMap = new HashMap<Identifier, Entity<?>>(aCol.size());
      Optional.ofNullable(aCol)
          .ifPresent(
              col ->
                  col.stream()
                      .filter(Objects::nonNull)
                      .forEach(
                          t -> {
                            var entity = (Entity<?>) t;
                            if (!put2IdEntityMap(aMap, entity)) {
                              // 不存在id，添加一个Add类型的EntityDiff
                              var diff = new EntityDiff(null, entity);
                              diff.setChangeType(ChangeType.Added);
                              collectionDiff.add(diff);
                            }
                          }));
      // 遍历sMap进行对比，aMap中没被访问到的情况是存在Id但是不存在快照，这在正常情况下不允许发生，应该被忽略
      sMap.forEach((id, snapshot) -> collectionDiff.add(diff(snapshot, aMap.get(id))));
      // 对比完成，判断对比结果
      if (collectionDiff.isEmpty()) {
        return null;
      }
    } else {
      // 其他类型直接使用equals方法比较，集合类均有重写equals方法，类似Arrays.deepEquals
      if (Objects.equals(sCol, aCol)) {
        return null;
      }
    }
    // 返回结果
    return collectionDiff;
  }

  private static boolean isNullOrUnidentified(Entity<?> entity) {
    return entity == null || entity.getId() == null;
  }

  private static boolean isNullOrEmpty(Collection<?> collection) {
    return collection == null || collection.isEmpty();
  }

  private static boolean allEntity(Collection<?> collection) {
    // 需要过滤null 否则instanceof会返回false
    return Optional.ofNullable(collection)
        .map(Collection::stream)
        .orElse(Stream.empty())
        .filter(Objects::nonNull)
        .allMatch(o -> o instanceof Entity);
  }

  private static boolean put2IdEntityMap(Map<Identifier, Entity<?>> map, Entity<?> entity) {
    // 快照不存在Id属于不正常情况，应该被忽略
    if (isNullOrUnidentified(entity)) {
      return false;
    }
    // TBD... Id重复情况下如何处理？
    map.put(entity.getId(), entity);
    return true;
  }

  public static class ChangeTrackingDiffException extends RuntimeException {

    public ChangeTrackingDiffException(Throwable cause) {
      super(cause);
    }
  }
}
