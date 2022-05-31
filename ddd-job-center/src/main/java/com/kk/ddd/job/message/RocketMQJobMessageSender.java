package com.kk.ddd.job.message;

import java.util.UUID;
import java.util.function.BooleanSupplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * RocketMQ采用了2PC的思想来实现了提交事务消息，同时增加一个补偿逻辑来处理二阶段超时或者失败的消息。<br>
 * 消息有三种状态： <br>
 * TransactionStatus.CommitTransaction：提交事务消息，消费者可以消费此消息。 <br>
 * TransactionStatus.RollbackTransaction：回滚事务，它代表该消息将被删除，不允许被消费。 <br>
 * TransactionStatus.Unknown ：中间状态，它代表需要检查消息队列来确定状态。<br>
 * 执行流程：<br>
 * 第一阶段：发送半消息，此时对消费者不可见；<br>
 * 第二阶段：本地事务执行完成后向MQServer发送Commit或Rollback操作，如果提交成功则消息才对消费者可见。<br>
 * 补偿逻辑：MQServer不断尝试向服务端发送回查请求以确认本地事务的执行状态，MQServer尝试次数达到阈值后默认执行回滚操作。<br>
 *
 * @author KaiKoo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RocketMQJobMessageSender implements JobMessageSender {

  private final RocketMQTemplate rocketMQTemplate;

  @Override
  public void send(String topic, String context, BooleanSupplier localAction) {
    log.info("execute job, topic '{}', context '{}'.", topic, context);
    var txId = UUID.randomUUID().toString();
    // 发送RocketMQ事务消息，如果发送成功arg参数会被传递到LocalTransactionListener。
    var transactionSendResult =
        rocketMQTemplate.sendMessageInTransaction(
            topic,
            MessageBuilder.withPayload(context)
                .setHeader(RocketMQHeaders.TRANSACTION_ID, txId)
                .build(),
            localAction);
    log.info("txId: '{}', transactionSendResult: '{}'.", txId, transactionSendResult);
  }
}
