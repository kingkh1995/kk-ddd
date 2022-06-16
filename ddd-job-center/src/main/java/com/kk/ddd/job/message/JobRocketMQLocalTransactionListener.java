package com.kk.ddd.job.message;

import com.kk.ddd.job.domain.LocalTxDAO;
import com.kk.ddd.job.domain.LocalTxDO;
import java.util.function.BooleanSupplier;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 事务消息监听器 <br>
 *
 * @author KaiKoo
 */
@Slf4j
@RocketMQTransactionListener //todo... 设置监听的rocketMQTemplateBeanName和执行监听任务的线程池属性
public class JobRocketMQLocalTransactionListener implements RocketMQLocalTransactionListener {

  public static final String TT_BEAN_NAME = "jobRocketMQLocalTransactionTemplate";

  public static final int PROPAGATION = TransactionDefinition.PROPAGATION_REQUIRES_NEW;

  private TransactionTemplate transactionTemplate;

  @Resource(name = TT_BEAN_NAME)
  public void setTransactionTemplate(
          TransactionTemplate transactionTemplate) {
    this.transactionTemplate = transactionTemplate;
  }

  private LocalTxDAO localTxDAO;

  @Autowired
  public void setLocalTxDAO(LocalTxDAO localTxDAO) {
    this.localTxDAO = localTxDAO;
  }

  // 为了支持回查本地事务的执行状态，需要在本地事务中一并将执行状态记录到数据表中。
  @Override
  public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
    var defaultState = RocketMQLocalTransactionState.ROLLBACK;
    // 获取事务ID
    if (!(msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID) instanceof String txId)) {
      return defaultState;
    }
    // 事务消息第二阶段：执行本地事务
    return transactionTemplate.execute(
        status -> {
          var start = status.createSavepoint();
          try {
            // 1.执行本地任务
            var localTx = new LocalTxDO();
            localTx.setId(txId);
            localTx.setState(defaultState);
            if (arg instanceof BooleanSupplier localAction) {
              try {
                if (localAction.getAsBoolean()) {
                  localTx.setState(RocketMQLocalTransactionState.COMMIT);
                }
              } catch (Exception e) {
                log.error("execute local action error!", e);
                // 本地任务失败则回滚本地任务
                status.rollbackToSavepoint(start);
              }
            }
            // 2.保存本地事务执行状态
            localTxDAO.save(localTx);
            return localTx.getState();
          } catch (Exception e) {
            log.error("save local transaction state error!", e);
            // 保存本地事务执行状态失败则全部回滚
            status.rollbackToSavepoint(start);
            return defaultState;
          }
        });
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
