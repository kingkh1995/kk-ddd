package com.kkk.op.support.base;

import java.util.Objects;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 策略模式基类
 *
 * @author KaiKoo
 */
public abstract class AbstractStrategyManager implements InitializingBean, ApplicationContextAware {

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = Objects.requireNonNull(applicationContext);
    }
}
