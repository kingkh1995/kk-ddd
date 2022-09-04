package com.kk.ddd.support.configuration;

import ch.qos.logback.classic.util.LogbackMDCAdapter;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.kk.ddd.support.annotation.LiteConfiguration;
import com.kk.ddd.support.bean.DistributedLockHelper;
import com.kk.ddd.support.bean.Kson;
import com.kk.ddd.support.distributed.DistributedLockFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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
        setJsonMapper2Kson(applicationContext);
        setFactory2EntityLocker(applicationContext);
        handleMDCAdapter();
    }

    // 设置factory到EntityLocker
    private void setJsonMapper2Kson(ApplicationContext applicationContext){
        try {
            var jsonMapper = applicationContext.getBean(JsonMapper.class);
            log.info("Set '{}' to Kson.", jsonMapper.getClass().getCanonicalName());
            Kson.setMapper(jsonMapper);
        } catch (BeansException e) {
            log.error("Set JsonMapper to Kson error!");
        }
    }

    // 设置factory到EntityLocker
    private void setFactory2EntityLocker(ApplicationContext applicationContext){
        try {
            var factory = applicationContext.getBean(DistributedLockFactory.class);
            log.info("Set '{}' to EntityLocker.", factory.getClass().getCanonicalName());
            DistributedLockHelper.setFactory(factory);
        } catch (BeansException e) {
            log.error("Set DistributedLockFactory to EntityLocker error!");
        }
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

}
