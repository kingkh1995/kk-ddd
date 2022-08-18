package com.kk.ddd.support.messaging;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 *
 * <br/>
 *
 * @author KaiKoo
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractMessageProducer implements MessageProducer{

    @Getter(AccessLevel.PROTECTED)
    private final MessageRecorder messageRecorder;

    @Override
    public void sendAtLeastOnce(String topic, String hashKey, @Size(min = 1) List<Message<?>> messages)
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
        // 先持久化，再注册事务同步器，事务提交后异步发送消息。
        TransactionSynchronizationManager.registerSynchronization(
                new AfterCommitTransactionSynchronizationAdaptor(
                        () -> this.doSendAtLeastOnceOrderly(this.getMessageRecorder().record(topic, hashKey, messages))));
    }

    protected void doSendAtLeastOnceOrderly(List<MessageModel> messageModels) throws MessagingException {
        // 顺序发送，前置消息发送完成后再发送后置消息。
        messageModels.stream()
                .map(this::doSendAsync)
                .reduce((left, right) -> {
                    right.setFuture(left.getFuture().thenComposeAsync(o -> {
                        this.messageRecorder.succeed(left.getId());
                        return right.getFuture();
                    }, this.messageRecorder.callbackExecutor()));
                    return right;
                })
                .get()
                .getFuture()
                .whenComplete((o, throwable) -> {
                    var messageModelIds = messageModels.stream().map(MessageModel::getId)
                            .collect(Collectors.toList());
                    if (throwable != null){
                        doAfterSendFailed(messageModelIds, throwable);
                    } else {
                        log.info("send orderly succeeded! messageModelIds: {}.", messageModelIds);
                    }
                });
    }

    protected void doAfterSendFailed(List<Long> messageModelIds, Throwable throwable) {
        log.error("send orderly failed! messageModelIds: {}.", messageModelIds, throwable);
    }

    protected MessageModel doSendAsync(MessageModel messageModel) throws MessagingException {
        messageModel.setFuture(doSendAsync(messageModel.getTopic(), messageModel.getHashKey(), messageModel.getMessage()));
        return messageModel;
    }


    abstract protected CompletableFuture<?> doSendAsync(String topic, String hashKey, Message<?> message)  throws MessagingException;
}
