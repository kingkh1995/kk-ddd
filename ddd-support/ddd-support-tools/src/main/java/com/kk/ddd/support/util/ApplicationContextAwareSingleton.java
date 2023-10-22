package com.kk.ddd.support.util;

import java.util.Objects;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 子类需要加上@Component注解 <br>
 * 执行顺序：setApplicationContext > @PostConstruct > InitializingBean > SmartInitializingSingleton
 *
 * @author KaiKoo
 */
public abstract class ApplicationContextAwareSingleton
    implements ApplicationContextAware, SmartInitializingSingleton {

  private ApplicationContext applicationContext;

  protected ApplicationContext getApplicationContext() {
    return Objects.requireNonNull(this.applicationContext);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    // 不要在此阶段通过applicationContext获取bean，会触发提前加载。
    if (this.applicationContext == null) {
      this.applicationContext = Objects.requireNonNull(applicationContext);
    }
  }
}
