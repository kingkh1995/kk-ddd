package com.kkk.op.support.tracking.diff;

import java.lang.invoke.SerializedLambda;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.validation.constraints.NotNull;

/**
 * lambda表达式实现方式为包含一个静态方法的内部类，序列化时实际上使用的类为 SerializedLambda <br>
 * 与匿名类相同的是都会生成一个内部类，不同是匿名类每次都会创建一个对象，而lambda表达式会被编译成调用静态方法实现。<br>
 * 所有lambda表达式只有在存在复用的情况才需要定义为常量使用。
 *
 * @author KaiKoo
 */
public class LambdaHelper {

  private LambdaHelper() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  // 缓存value使用SoftReference，无需清理，因为lambda表达式创建后对象就一直被持有，所以key永远都不会被回收。
  private static final Map<SFunction<?, ?>, SoftReference<SerializedLambda>> FUNC_CACHE =
      new ConcurrentHashMap<>();

  /**
   * 对象序列化的相关方法： <br>
   * writeReplace：在将对象序列化之前，如果对象的类或父类中存在writeReplace方法，则使用writeReplace的返回值作为真实被序列化的对象，在writeObject之前执行；
   * <br>
   * readResolve：在将对象反序列化之后，ObjectInputStream.readObject返回之前，如果从对象流中反序列化得到的对象所属类或父类中存在readResolve方法，
   * 则使用readResolve的返回值作为ObjectInputStream.readObject的返回值，在readObject之后执行； <br>
   * <br>
   * lambda表达式可以被序列化，编译器会为继承了Serializable的lambda表达式自动添加writeReplace方法，而SerializedLambda类中定义了readResolve方法。
   */
  public static SerializedLambda resolve(@NotNull SFunction<?, ?> func) {
    return Optional.ofNullable(FUNC_CACHE.get(func))
        .map(SoftReference::get)
        .orElseGet(
            () -> {
              var clazz = func.getClass();
              // 要求是合成类，即由编译器创建，lambda表达式属于合成类，匿名类不属于。
              if (!clazz.isSynthetic()) {
                throw new IllegalArgumentException(
                    "Error resolving lambda for class '"
                        + clazz.getTypeName() // 使用getTypeName可以获取到匿名类的类名
                        + "' should be synthetic!");
              }
              try {
                var writeReplace = clazz.getDeclaredMethod("writeReplace");
                writeReplace.trySetAccessible();
                var lambda = (SerializedLambda) writeReplace.invoke(func);
                FUNC_CACHE.put(func, new SoftReference<>(lambda));
                return lambda;
              } catch (Exception e) {
                throw new IllegalArgumentException("Here is impossible to reach!", e);
              }
            });
  }
}
