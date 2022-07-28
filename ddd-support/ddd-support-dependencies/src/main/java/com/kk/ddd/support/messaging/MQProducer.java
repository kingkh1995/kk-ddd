package com.kk.ddd.support.messaging;

import com.kk.ddd.support.exception.MQException;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import org.springframework.messaging.Message;

/**
 * 领域事件发送
 * <br/>
 * todo... 添加RocketMQ、Kafka、Redis实现，以及延时消息。
 *
 * @author KaiKoo
 */
public interface MQProducer {

    /**
     * 普通发送，不保证可靠，可以是同步或异步，默认会是异步批量发送。
     * @param message 消息
     */
    void send(Message<?> message) throws MQException;

    /**
     * 可靠发送
     * @param message 消息
     */
    default void sendAtLeastOnce(Message<?> message) throws MQException {
        sendAtLeastOnce(Collections.singletonList(message));
    }

    /**
     * 可靠发送，至少成功一次，消费端需要防止重复消费，使用本地消息表解决方案实现。
     * @param messages 消息列表，按顺序发送。
     */
    void sendAtLeastOnce(List<Message<?>> messages) throws MQException;

    /**
     * 可靠事务消息，可能发送失败。仅RocketMQ支持，首先发送半消息，收到响应后执行监听器，在监听器中执行本地事务。
     * @param message 消息
     * @param localAction 本地操作
     * @return 返回执行结果，失败原因可以是半消息发送失败或本地操作执行失败。
     */
    default boolean sendInTransaction(Message<?> message, BooleanSupplier localAction) throws MQException {
        return false;
    }

}
