package com.kkk.op.support.changeTracking.diff;

import com.kkk.op.support.changeTracking.exception.BussinessException;
import com.kkk.op.support.markers.Entity;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 *
 * @author KaiKoo
 */
public class DiffUtil {

    public static <T extends Entity> EntityDiff diff(@NotNull T snapshot, @NotNull T aggregate) {
        EntityDiff entityDiff = new EntityDiff(snapshot, aggregate);
        Field[] fields = snapshot.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object sObj = field.get(snapshot);
                Object obj = field.get(aggregate);
                if (snapshot instanceof Entity) {
                    entityDiff.put(field.getName(), diff((Entity) sObj, (Entity) obj));
                } else if (snapshot instanceof Collection) {
                    CollectionDiff collectionDiff = new CollectionDiff();
                    entityDiff.put(field.getName(), collectionDiff);
                } else {
                    // 其他类型：Type和基本数据类型
                    if (!entityDiff.isSelfModified() && !Objects.equals(sObj, obj)) {
                        entityDiff.setSelfModified(true);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new BussinessException(e);
            }
        }
        return entityDiff;
    }
}
