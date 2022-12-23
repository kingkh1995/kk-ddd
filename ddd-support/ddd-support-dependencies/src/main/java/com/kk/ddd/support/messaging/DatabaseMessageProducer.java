package com.kk.ddd.support.messaging;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

/**
 * 基于数据库的消息队列（本地消息表），最可靠成本最低的方式。<br>
 * TODO... <br>
 *
 * @author KaiKoo
 */
public class DatabaseMessageProducer extends AbstractMessageProducer {

  public DatabaseMessageProducer(final MessageStorage messageStorage) {
    super(messageStorage);
  }

  @Override
  protected CompletableFuture<?> doSendAsync(String topic, String hashKey, Message<?> message)
      throws MessagingException {
    return null;
  }

  @Override
  public void send(String topic, String hashKey, Message<?> message) {}

  @Override
  public boolean sendInTransaction(String topic, Message<?> message, BooleanSupplier localAction)
      throws MessagingException {
    return false;
  }
}
