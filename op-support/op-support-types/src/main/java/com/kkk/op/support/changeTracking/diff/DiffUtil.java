package com.kkk.op.support.changeTracking.diff;

import com.kkk.op.support.marker.Entity;
import com.kkk.op.support.marker.Identifier;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 *
 * @author KaiKoo
 */
public class DiffUtil {

    /**
     * 只保存了Entity类和Collection的对比信息
     * TBD... 其他字段的对比信息是否添加待定，会增大内存消耗
     */
    public static <T extends Entity> EntityDiff diff(T snapshot, T aggregate) {
        // 均无法标识返回Null
        if (noneIdentifier(snapshot) && noneIdentifier(aggregate)) {
            return null;
        }
        EntityDiff entityDiff = new EntityDiff(snapshot, aggregate);
        // snapshot无法标识，对比结果为新增
        if (noneIdentifier(snapshot)) {
            entityDiff.setType(DiffType.Added);
            return entityDiff;
        }
        // aggregate无法标识，对比结果为移除
        if (noneIdentifier(aggregate)) {
            entityDiff.setType(DiffType.Removed);
            return entityDiff;
        }
        if (!Objects.equals(snapshot.getId(), aggregate.getId())) {
            return null;
        }
        if (!Objects.equals(snapshot.getClass(), aggregate.getClass())) {
            entityDiff.setType(DiffType.Modified);
            return entityDiff;
        }
        Field[] fields = snapshot.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object sObj = field.get(snapshot);
                Object aObj = field.get(aggregate);
                if (sObj instanceof Entity) {
                    entityDiff.put(field.getName(), diff((Entity) sObj, (Entity) aObj));
                } else if (sObj instanceof Collection) {
                    entityDiff.put(field.getName(), diff((Collection) sObj, (Collection) aObj));
                } else {
                    // 其他类型：Type和基本数据类型
                    if (!entityDiff.isSelfModified() && !Objects.equals(sObj, aObj)) {
                        entityDiff.setSelfModified(true);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return entityDiff;
    }

    public static <T> CollectionDiff diff(@NotNull Collection<T> sCol,
            @NotNull Collection<T> aCol) {
        if (isEmpty(sCol) && isEmpty(aCol)) {
            return null;
        }
        CollectionDiff collectionDiff = new CollectionDiff(sCol, aCol);
        if (isEmpty(sCol)) {
            collectionDiff.setType(DiffType.Added);
            return collectionDiff;
        }
        if (isEmpty(aCol)) {
            collectionDiff.setType(DiffType.Removed);
            return collectionDiff;
        }
        Iterator<T> iterator = sCol.iterator();
        T obj = iterator.next();
        Class<?> clazz = obj.getClass();
        //为什么不对比所有元素的Class去快速失败，因为异常情况为少数，大部分情况都是正常，减少遍历次数
    /*while (iterator.hasNext()) {
        if (!Objects.equals(clazz, iterator.next().getClass())) {
            collectionDiff.setType(DiffType.Modified);
            return collectionDiff;
        }
    }
    iterator = aCol.iterator();
    while (iterator.hasNext()) {
        if (!Objects.equals(clazz, iterator.next().getClass())) {
            collectionDiff.setType(DiffType.Modified);
            return collectionDiff;
        }
    }*/
        if (obj instanceof Entity) {
            Iterator<T> sIterator = sCol.iterator();
            Map<Identifier, Entity> sMap = new HashMap<>();
            while (sIterator.hasNext()) {
                Entity entity = (Entity) sIterator.next();
                if (entity.getId() != null) {
                    sMap.put(entity.getId(), entity);
                }
            }
            Iterator<T> aIterator = aCol.iterator();
            Map<Identifier, Entity> aMap = new HashMap();
            while (aIterator.hasNext()) {
                Entity entity = (Entity) aIterator.next();
                if (entity.getId() != null) {
                    aMap.put(entity.getId(), entity);
                }
            }
            Map<Identifier, Entity> unionMap = new HashMap();
        } else {
            if (!Objects.equals(sCol, aCol)) {
                collectionDiff.setType(DiffType.Modified);
            }
            return collectionDiff;
        }
        return collectionDiff;
    }

    private static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    private static boolean noneIdentifier(Entity entity) {
        return entity == null || entity.getId() == null;
    }
}
