package com.kkk.op.support.accessCondition;

import com.kkk.op.support.annotations.AccessCondition;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * AccessCondition切面 <br>
 * todo...
 *
 * @author KaiKoo
 */
@Aspect
public class AccessConditionAspect implements InitializingBean, ApplicationContextAware {

  protected ApplicationContext applicationContext;

  protected Map<String, AccessConditionCheckPlugin> pluginMap;

  @After("@annotation(com.kkk.op.support.annotations.AccessCondition)")
  public void accessConditionCheck(JoinPoint joinPoint) {}

  @Override
  public void afterPropertiesSet() throws Exception {
    if (pluginMap != null) {
      return;
    }
    this.pluginMap =
        this.applicationContext.getBeansOfType(AccessConditionCheckPlugin.class).values().stream()
            .collect(Collectors.toMap(AccessConditionCheckPlugin::name, Function.identity()));
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = Objects.requireNonNull(applicationContext);
  }

  private AccessConditionCheckPlugin getPlugin(String name) {
    if (name == null || name.isBlank() || AccessCondition.DEFUALT.equalsIgnoreCase(name)) {
      return null;
    }
    return this.pluginMap.get(name);
  }
}
