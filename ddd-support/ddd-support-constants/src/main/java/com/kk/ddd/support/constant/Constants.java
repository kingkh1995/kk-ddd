package com.kk.ddd.support.constant;

import java.util.ServiceLoader;

/**
 * 常量工具类，只能通过SPI方式提供拓展。 <br>
 * 三种SPI方式的对比：<br>
 * Java：使用ServiceLoader，配置路径为META-INF/services/，缺点是按classpath的加载顺序加载类，故无法确定顺序。<br>
 * Spring：使用SpringFactoriesLoader，在META-INF/spring.factories中配置所有拓展点，使用【接口=具体类名1,具体类名2】方式配置，自定义的配置文件会被首先加载。
 * <br>
 * Dubbo：使用ExtensionLoader，配置路径为META-INF/dubbo/，使用键值对的方式配置，接口上还需要添加@SPI注解，可指定默认实现。<br>
 *
 * @author KaiKoo
 */
public class Constants {

  private Constants() throws IllegalAccessException {
    throw new IllegalAccessException();
  }

  public static final BaseConstantsProvider BASE =
      ServiceLoader.load(BaseConstantsProvider.class)
          .findFirst()
          .orElseGet(() -> new BaseConstantsProvider() {});

  public static final TypeConstantsProvider TYPE =
      ServiceLoader.load(TypeConstantsProvider.class)
          .findFirst()
          .orElseGet(() -> new TypeConstantsProvider() {});

  public static final ExecutorConstantsProvider EXECUTOR =
      ServiceLoader.load(ExecutorConstantsProvider.class)
          .findFirst()
          .orElseGet(() -> new ExecutorConstantsProvider() {});
}
