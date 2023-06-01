package com.kk.ddd.support.util;

import java.lang.invoke.SerializedLambda;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.validation.constraints.NotNull;

/**
 * 解析lambda表达式和方法引用 <br>
 *
 * @author KaiKoo
 */
public final class LambdaHelper {
  private LambdaHelper() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  private static final ConcurrentMap<
          Class<? extends SFunction<?, ?>>, SoftReference<SerializedLambda>>
      CACHE = new ConcurrentHashMap<>();

  /**
   * 对象序列化的相关方法： <br>
   * writeReplace：在将对象序列化之前，如果对象的类或父类中存在writeReplace方法，则使用writeReplace的返回值作为真实被序列化的对象，在writeObject之前执行；
   * <br>
   * readResolve：在将对象反序列化之后，ObjectInputStream.readObject返回之前，如果从对象流中反序列化得到的对象所属类或父类中存在readResolve方法，
   * 则使用readResolve的返回值作为ObjectInputStream.readObject的返回值，在readObject之后执行； <br>
   * <br>
   */
  public static <S extends SFunction<T, R>, T, R> SerializedLambda resolve(@NotNull S func) {
    var clazz = (Class<S>) func.getClass();
    // 判断是否是合成类，使用getTypeName可以获取到内部类的类名
    if (!clazz.isSynthetic()) {
      throw new IllegalArgumentException(
          "Error, for '%s' should be synthetic!".formatted(clazz.getTypeName()));
    }
    return CACHE
        .compute(
            clazz,
            (k, v) -> {
              if (v != null && v.get() != null) {
                return v;
              }
              try {
                var writeReplace = k.getDeclaredMethod("writeReplace");
                writeReplace.trySetAccessible();
                return new SoftReference<>((SerializedLambda) writeReplace.invoke(func));
              } catch (Exception e) {
                throw new IllegalArgumentException("Here is impossible to reach!", e);
              }
            })
        .get();
  }
}
