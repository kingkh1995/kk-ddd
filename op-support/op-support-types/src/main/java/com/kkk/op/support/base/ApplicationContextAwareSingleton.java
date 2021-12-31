package com.kkk.op.support.base;

import java.util.Objects;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 子类需要加上@Component注解 <br>
 * 执行顺序：@PostConstruct > InitializingBean > SmartInitializingSingleton
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
    if (this.applicationContext == null) {
      this.applicationContext = Objects.requireNonNull(applicationContext);
    }
  }
}
