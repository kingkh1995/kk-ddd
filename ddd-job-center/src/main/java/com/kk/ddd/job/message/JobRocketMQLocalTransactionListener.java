package com.kk.ddd.job.message;

import com.kk.ddd.job.domain.LocalTxDAO;
import com.kk.ddd.job.domain.LocalTxDO;
import java.util.function.BooleanSupplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 事务消息监听器 <br>
 *
 * @author KaiKoo
 */
@Slf4j
@RequiredArgsConstructor
@RocketMQTransactionListener //todo... 设置监听的rocketMQTemplateBeanName和执行监听任务的线程池属性
public class JobRocketMQLocalTransactionListener implements RocketMQLocalTransactionListener {

  // REQUIRES_NEW为额外开启新事务，与外部事务互不影响，而NESTED会被外部事务影响但不会影响外部事务。
  private static final TransactionDefinition TD =
      new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

  private final PlatformTransactionManager transactionManager;

  private final LocalTxDAO localTxDAO;

  // 为了支持回查本地事务的执行状态，需要在本地事务中一并将执行状态记录到数据表中。
  @Override
  public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
    var localTransactionState = RocketMQLocalTransactionState.ROLLBACK;
    // 获取事务ID
    if (!(msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID) instanceof String txId)) {
      return localTransactionState;
    }
    // 第二阶段执行本地事务
    var transaction = transactionManager.getTransaction(TD);
    try {
      // 1.执行本地任务
      if (arg instanceof BooleanSupplier localAction) {
        var savepoint = transaction.createSavepoint();
        try {
          if (localAction.getAsBoolean()) {
            localTransactionState = RocketMQLocalTransactionState.COMMIT;
          }
        } catch (Exception e) {
          log.error("execute local action error!", e);
          // 本地任务异常则回滚本地任务
          transaction.rollbackToSavepoint(savepoint);
        }
      }
      // 2.保存本地事务执行状态
      localTxDAO.save(new LocalTxDO().setTxId(txId).setState(localTransactionState));
      // 3.提交本地事务
      transactionManager.commit(transaction);
    } catch (Exception e) {
      log.error("save local transaction state error!", e);
      transactionManager.rollback(transaction);
    }
    return localTransactionState;
  }

  @Override
  public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
    if (msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID) instanceof String txId) {
      return localTxDAO
          .findById(txId)
          .map(LocalTxDO::getState)
          .orElse(RocketMQLocalTransactionState.UNKNOWN);
    }
    return RocketMQLocalTransactionState.UNKNOWN;
  }
}
