package com.kk.ddd.support.messaging;

import java.util.function.BooleanSupplier;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

/**
 * 消息发送
 * <br/>
 * todo... 添加RocketMQ、Kafka、Spring Cloud Stream实现，以及延时消息。
 *
 * @author KaiKoo
 */
public interface MessageProducer {

    default void send(String topic, Message<?> message) throws MessagingException {
        send(topic, null, message);
    }

    /**
     * 普通发送，不保证成功，默认会是批量异步单向发送。
     *
     * @param topic 话题
     * @param hashKey 哈希键-用于分区
     * @param message 消息
     * @throws MessagingException 非特殊情况下不应该抛出异常
     */
    void send(@NotBlank String topic, String hashKey, @NotNull Message<?> message) throws MessagingException;

    default void sendAtLeastOnce(@NotBlank String topic, @NotNull Message<?> message) throws MessagingException {
        sendAtLeastOnce(topic, null, message);
    }

    /**
     * 可靠发送，at-least-once，持久化消息到数据库，本地事务提交后再异步发送消息，并使用定时任务补偿保证。<br/>
     * 注意：可能发送多次，消费端需要保证幂等。
     *
     * @param topic 话题
     * @param hashKey 哈希键-用于分区
     * @param messages 消息列表
     * @throws MessagingException 本地事务提交成功后就不应该抛出异常
     */
    void sendAtLeastOnce(@NotBlank String topic, String hashKey, @Size(min = 1) Message<?>... messages) throws MessagingException;

    /**
     * 可靠事务消息，首先发送半消息，收到响应后执行监听器，在监听器中执行本地事务。
     *
     * @param topic 话题
     * @param message 消息
     * @param localAction 本地事务
     * @return 返回执行结果，失败原因可以是半消息发送失败或本地操作执行失败。
     * @throws MessagingException 异常信息
     */
    boolean sendInTransaction(@NotBlank String topic, @NotNull Message<?> message, @NotNull BooleanSupplier localAction) throws MessagingException;

}
