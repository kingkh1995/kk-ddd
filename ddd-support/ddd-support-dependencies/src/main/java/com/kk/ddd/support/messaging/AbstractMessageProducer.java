package com.kk.ddd.support.messaging;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractMessageProducer implements MessageProducer {

  @Getter(AccessLevel.PROTECTED)
  private final MessageStorage messageStorage;

  @Override
  public void sendAtLeastOnce(
      String topic, String hashKey, @Size(min = 1) List<Message<?>> messages)
      throws MessagingException {
    if (Objects.isNull(messages) || messages.isEmpty()) {
      throw new IllegalArgumentException("messages can't be empty!");
    } else if (!TransactionSynchronizationManager.isSynchronizationActive()) {
      // 表示事务同步器是否激活，事务创建时会执行initSynchronization方法，激活事务同步器。
      throw new IllegalStateException("Transaction synchronization is not active!");
    } else if (!TransactionSynchronizationManager.isActualTransactionActive()) {
      // 表示当前是否有一个实际事务是激活的，由于隔离级别的问题，当前可能不存在激活的实际事务。
      throw new IllegalStateException("Actual transaction is not active!");
    } else if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
      // 表示当前事务是只读
      throw new IllegalStateException("Current transaction is read-only!");
    }
    // 先持久化
    List<MessageModel> messageModels = this.getMessageStorage().save(topic, hashKey, messages);
    // 再注册事务同步器，在事务提交后异步发送消息。
    TransactionSynchronizationManager.registerSynchronization(
        new AfterCommitTransactionSynchronizationAdaptor(
            () -> this.doSendAtLeastOnceOrderly(messageModels)));
  }

  protected void doSendAtLeastOnceOrderly(List<MessageModel> messageModels)
      throws MessagingException {
    if (messageModels == null || messageModels.isEmpty()) {
      return;
    }
    // 顺序发送，前置消息发送完成后再发送后置消息。
    messageModels.stream()
        .map(this::doSendAsync)
        .reduce(
            (former, cur) -> {
              cur.setFuture(
                  former
                      .getFuture()
                      .thenComposeAsync(
                          o -> {
                            this.messageStorage.complete(former.getId());
                            return cur.getFuture();
                          },
                          this.callbackExecutor()));
              return cur;
            })
        .get()
        .getFuture()
        .whenComplete(
            (o, throwable) -> {
              if (throwable == null) {
                doAfterSendSucceeded(messageModels);
              } else {
                doAfterSendFailed(messageModels, throwable);
              }
            });
  }

  protected MessageModel doSendAsync(MessageModel messageModel) throws MessagingException {
    messageModel.setFuture(
        doSendAsync(messageModel.getTopic(), messageModel.getHashKey(), messageModel.getMessage()));
    return messageModel;
  }

  protected void doAfterSendSucceeded(List<MessageModel> messageModels) {
    var messageModelIds = messageModels.stream().map(MessageModel::getId).toList();
    log.info("send orderly succeeded! messageModelIds: {}.", messageModelIds);
  }

  protected void doAfterSendFailed(List<MessageModel> messageModels, Throwable throwable) {
    var failedModelIds =
        messageModels.stream()
            .filter(messageModel -> messageModel.getFuture().isCompletedExceptionally())
            .map(MessageModel::getId)
            .toList();
    log.error("send orderly failed! failedModelIds: {}.", failedModelIds, throwable);
  }

  protected ExecutorService callbackExecutor() {
    return ForkJoinPool.commonPool();
  }

  protected abstract CompletableFuture<?> doSendAsync(
      String topic, String hashKey, Message<?> message) throws MessagingException;
}
