package com.kkk.op.support.config;

import ch.qos.logback.classic.util.LogbackMDCAdapter;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.kkk.op.support.annotation.LiteConfiguration;
import com.kkk.op.support.base.EntityLocker;
import com.kkk.op.support.marker.DistributedLockFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * 后置配置
 * <br/>
 *
 * @author KaiKoo
 */
@Slf4j
@LiteConfiguration
public class PostConfiguration implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setFactory2EntityLocker(applicationContext);
        handleMDCAdapter();
    }

    // 处理MDCAdapter，内部实现为ThreadLocal
    private void handleMDCAdapter() {
        var adapter = MDC.getMDCAdapter();
        if(adapter instanceof LogbackMDCAdapter logbackMDCAdapter){
            try {
                var field = logbackMDCAdapter.getClass().getDeclaredField("copyOnThreadLocal");
                field.trySetAccessible();
                // ttl手动注册方式
                TransmittableThreadLocal.Transmitter.registerThreadLocal((ThreadLocal<?>) field.get(logbackMDCAdapter), v -> v);
            } catch (Exception e) {
                log.error("Handle LogbackMDCAdapter error!", e);
            }
        }
    }

    // 设置factory到EntityLocker
    private void setFactory2EntityLocker(ApplicationContext applicationContext){
        var factory = applicationContext.getBean(DistributedLockFactory.class);
        Assert.notNull(factory, "DistributedLockFactory should exist!");
        log.info("Set '{}' to EntityLocker.", factory.getClass().getCanonicalName());
        EntityLocker.setFactory(factory);
    }
}
