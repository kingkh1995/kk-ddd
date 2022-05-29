package com.kkk.op.support.util;

import java.lang.invoke.SerializedLambda;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.validation.constraints.NotNull;

/**
 * 匿名类会被编译为class文件，每次都会创建一个对象，所以this指向的是匿名类对象。<br>
 * lambda表达式和方法引用不会被编译为class文件，其实是被封装成了主类的一个私有方法（通过invokedynamic指令调用），即this指向外围实例； <br>
 * 属于合成类，表示是由编译器创建的，只会生成一个class对象，在序列化时实际上使用的类为 SerializedLambda； <br>
 * 如果捕获了实例参数，则每次都会创建一个新的对象，这种情况下需要考虑定义为常量使用。<br>
 *
 * @author KaiKoo
 */
public class LambdaHelper {
  private LambdaHelper() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  private static final Map<Class<? extends SFunction<?, ?>>, SoftReference<SerializedLambda>>
      CACHE = new ConcurrentHashMap<>();
  /**
   * 对象序列化的相关方法： <br>
   * writeReplace：在将对象序列化之前，如果对象的类或父类中存在writeReplace方法，则使用writeReplace的返回值作为真实被序列化的对象，在writeObject之前执行；
   * <br>
   * readResolve：在将对象反序列化之后，ObjectInputStream.readObject返回之前，如果从对象流中反序列化得到的对象所属类或父类中存在readResolve方法，
   * 则使用readResolve的返回值作为ObjectInputStream.readObject的返回值，在readObject之后执行； <br>
   * <br>
   * SerializedLambda： <br>
   * lambda表达式可以被序列化，编译器会为继承了Serializable的lambda表达式添加writeReplace方法，将其转换为SerializedLambda对象； <br>
   * 且在SerializedLambda类中定义了readResolve方法，用于反序列化SerializedLambda对象为lambda表达式。 <br>
   * lambda表达式implClass为主类的类名，implMethodName为编译器生成的方法名，以 lambda$main$ 开头；<br>
   * 如果是方法引用，implClass为其类名，implMethodName为其方法名。<br>
   * capturedArgs为捕获的参数，如果是静态方法引用则固定为空，如果是实例方法引用则必然包含实例本身。
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
