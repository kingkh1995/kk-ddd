package com.kk.ddd.support.messaging;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;

/**
 *
 * <br/>
 *
 * @author KaiKoo
 */
public class KafkaMessageProducer extends AbstractMessageProducer{

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaMessageProducer(MessageRecorder messageRecorder,
            KafkaTemplate<String, Object> kafkaTemplate) {
        super(messageRecorder);
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    protected CompletableFuture<?> doSendAsync(String topic, String hashKey, Message<?> message) {
        kafkaTemplate.send(
                MessageBuilder.withPayload(message.getPayload())
                        .copyHeaders(message.getHeaders())
                        .setHeader(KafkaHeaders.TOPIC, topic)
                        .setHeader(KafkaHeaders.MESSAGE_KEY, hashKey)
                        .build());
        return null; // fixme... usingCompletableFuture
    }

    @Override
    public void send(String topic, String hashKey, Message<?> message) throws MessagingException {
        doSendAsync(topic, hashKey, message);
    }

    @Override
    public boolean sendInTransaction(String topic, Message<?> message, BooleanSupplier localAction)
            throws MessagingException {
        return false;
    }
}
