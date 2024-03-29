package com.kk.ddd.support.messaging;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

/**
 * <br>
 *
 * @author KaiKoo
 */
public class RocketMQMessageProducer extends AbstractMessageProducer {

  private final RocketMQTemplate rocketMQTemplate;

  public RocketMQMessageProducer(
      final MessageStorage messageStorage, final RocketMQTemplate rocketMQTemplate) {
    super(messageStorage);
    this.rocketMQTemplate = rocketMQTemplate;
  }

  @Override
  protected CompletableFuture<?> doSendAsync(String topic, String hashKey, Message<?> message)
      throws MessagingException {
    var completableFuture = new CompletableFuture<>();
    rocketMQTemplate.asyncSendOrderly(
        topic,
        message,
        hashKey,
        new SendCallback() {
          @Override
          public void onSuccess(SendResult sendResult) {
            completableFuture.complete(sendResult);
          }

          @Override
          public void onException(Throwable e) {
            completableFuture.completeExceptionally(e);
          }
        });
    return completableFuture;
  }

  @Override
  public void send(String topic, String hashKey, Message<?> message) {
    rocketMQTemplate.sendOneWayOrderly(topic, message, hashKey);
  }

  @Override
  public boolean sendInTransaction(String topic, Message<?> message, BooleanSupplier localAction)
      throws MessagingException {
    return false;
  }
}
