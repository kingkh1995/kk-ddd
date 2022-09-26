package com.kk.ddd.support.messaging;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import org.springframework.kafka.core.KafkaOperations2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class KafkaMessageProducer extends AbstractMessageProducer {

  private final KafkaOperations2<String, Object> kafkaOperations;

  public KafkaMessageProducer(
      MessageStorage messageStorage, KafkaTemplate<String, Object> kafkaTemplate) {
    super(messageStorage);
    this.kafkaOperations = kafkaTemplate.usingCompletableFuture();
  }

  @Override
  protected CompletableFuture<?> doSendAsync(String topic, String hashKey, Message<?> message)
      throws MessagingException {
    return kafkaOperations.send(
        MessageBuilder.withPayload(message.getPayload())
            .copyHeaders(message.getHeaders())
            .setHeader(KafkaHeaders.TOPIC, topic)
            .setHeader(KafkaHeaders.KEY, hashKey)
            .build());
  }

  @Override
  public void send(String topic, String hashKey, Message<?> message) {
    doSendAsync(topic, hashKey, message);
  }

  @Override
  public boolean sendInTransaction(String topic, Message<?> message, BooleanSupplier localAction)
      throws MessagingException {
    return false;
  }
}
