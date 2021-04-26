package com.kkk.op.support.changeTracking.diff;

import com.kkk.op.support.base.Entity;
import com.kkk.op.support.exception.BussinessException;
import com.kkk.op.support.marker.Identifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotEmpty;

/**
 *
 * @author KaiKoo
 */
public final class DiffUtil {// 工具类声明为 final

    // 保证不能被实例化，同时防止反射机制创建对象
    private DiffUtil() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    /**
     * 只保存了 Entity 和 Collection 的对比信息 1）减少内存消耗 2）详细的对比信息必要性不高
     * TBD... Type类型字段的对比信息是否添加待定
     *
     * @return 如无对比不同则返回null
     */
    public static <T extends Entity> EntityDiff diff(T snapshot, T aggregate) {
        // 均无法标识返回Null
        if (isNullOrUnidentified(snapshot) && isNullOrUnidentified(aggregate)) {
            return null;
        }
        final var entityDiff = new EntityDiff(snapshot, aggregate);
        // snapshot无法标识，对比结果为新增
        if (isNullOrUnidentified(snapshot)) {
            entityDiff.setType(DiffType.Added);
            return entityDiff;
        }
        // aggregate无法标识，对比结果为移除
        if (isNullOrUnidentified(aggregate)) {
            entityDiff.setType(DiffType.Removed);
            return entityDiff;
        }
        // 均可标识，但是标识符不一致，正常情况下不可能发生
        if (!Objects.equals(snapshot.getId(), aggregate.getId())) {
            // todo... 待定
            return null;
        }
        // 类型不一致（同一个类的不同子类），不需对比，对比结果为修改，selfModified设为true
        if (!Objects.equals(snapshot.getClass(), aggregate.getClass())) {
            entityDiff.setType(DiffType.Modified);
            entityDiff.setSelfModified(true);
            return entityDiff;
        }
        // 开始对比
        var fields = snapshot.getClass().getDeclaredFields();
        for (var field : fields) {
            field.trySetAccessible();
            try {
                var sObj = field.get(snapshot);
                var aObj = field.get(aggregate);
                // todo... 处理null的特殊场景 instanceof关键字如果obj为null会直接返回false
                // 根据类型做不同的处理
                if (sObj instanceof Entity && aObj instanceof Entity) {
                    entityDiff.put(field.getName(), diff((Entity) sObj, (Entity) aObj));
                } else if (sObj instanceof Collection && aObj instanceof Collection) {
                    entityDiff.put(field.getName(), diff((Collection) sObj, (Collection) aObj));
                } else {
                    // 其他类型 Type （DP和基本数据类型，不应该有Map类型，设计中应该将Map替换为DP或Entity）
                    // 如果存在不同则修改 selfModified 参数，不添加到 EntityDiff 的 Map
                    if (!entityDiff.isSelfModified() && !Objects.equals(sObj, aObj)) {
                        entityDiff.setSelfModified(true);
                    }
                }
            } catch (Exception e) {
                // CheckedException 转为 RuntimeException
                throw new BussinessException(e);
            }
        }
        // 对比完成，判断对比结果
        if (entityDiff.isEmpty() && !entityDiff.isSelfModified()) {
            return null;
        }
        // 设置类型为 Modified 并返回对比结果
        entityDiff.setType(DiffType.Modified);
        return entityDiff;
    }

    /**
     *
     * @param sCol 不能包含空元素
     * @param aCol 不能包含空元素
     * @return 如无对比不同则返回null
     */
    public static <T> CollectionDiff diff(Collection<T> sCol, Collection<T> aCol) {
        if (isNullOrEmpty(sCol) && isNullOrEmpty(aCol)) {
            return null;
        }
        final var collectionDiff = new CollectionDiff(sCol, aCol);
        if (isNullOrEmpty(sCol)) {
            collectionDiff.setType(DiffType.Added);
            return collectionDiff;
        }
        if (isNullOrEmpty(aCol)) {
            collectionDiff.setType(DiffType.Removed);
            return collectionDiff;
        }
        // 开始对比 集合元素应该 全部为Type类型 或 全部为Entity类型，而不应该为Collection或者Map，设计时应被替换为DP或Entity
        // 判断所有元素是否全是Entity并过滤空元素 运行时已经擦除了泛型信息，所以无法通过反射获取到实际的类型
        if (allEntity(aCol) && allEntity(sCol)) {
            // 如元素类型为Entity 根据Id去查找匹配
            // 拼装sMap
            var sMap = new HashMap<Identifier, Entity>();
            sCol.stream().filter(Objects::nonNull).forEach(t -> put2IdEntityMap(sMap, (Entity) t));
            // 拼装aMap
            var aMap = new HashMap<Identifier, Entity>();
            aCol.stream().filter(Objects::nonNull).forEach(t -> {
                var entity = (Entity) t;
                if (put2IdEntityMap(aMap, entity) == null) {
                    // 不存在id，添加一个Add类型的EntityDiff
                    var diff = new EntityDiff(null, entity);
                    diff.setType(DiffType.Added);
                    collectionDiff.add(diff);
                }
            });
            // 遍历sMap进行对比，aMap中没被访问到的情况是存在Id但是不存在快照，这在正常情况下不允许发生，应该被忽略
            sMap.forEach((id, snapshot) -> collectionDiff.add(diff(snapshot, aMap.get(id))));
            // 对比完成，判断对比结果
            if (collectionDiff.isEmpty()) {
                return null;
            }
        } else {
            // 其他类型直接使用 equals 方法比较
            if (Objects.equals(sCol, aCol)) {
                return null;
            }
        }
        // 返回结果
        collectionDiff.setType(DiffType.Modified);
        return collectionDiff;
    }


    private static boolean isNullOrUnidentified(Entity entity) {
        return entity == null || entity.getId() == null;
    }

    private static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    private static boolean allEntity(@NotEmpty Collection<?> collection) {
        return collection.stream().filter(Objects::nonNull).allMatch(o -> o instanceof Entity);
    }

    private static Entity put2IdEntityMap(Map<Identifier, Entity> map, Entity entity) {
        // 快照不存在Id属于不正常情况，应该被忽略
        if (isNullOrUnidentified(entity)) {
            return null;
        }
        return map.put(entity.getId(), entity);
    }
}
