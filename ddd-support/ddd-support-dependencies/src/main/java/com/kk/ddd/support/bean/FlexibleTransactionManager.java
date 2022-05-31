package com.kk.ddd.support.bean;

import java.util.LinkedList;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 柔性事务<br>
 * TCC模式，存在锁定态，适用于一致性要求高的短事务，失败策略适合回滚，因为锁定态为不可用状态，持续时间不宜过长。
 * Saga模式，适用于一致性要求低的长事务，失败策略适合重试，流程及开发简单，但要求业务上能容忍长时间的软状态。 <br>
 *
 * @author KaiKoo
 */
@Slf4j
public class FlexibleTransactionManager {

  private final LinkedList<Runnable> runnables = new LinkedList<>();

  /** TCC柔性事务，所有参与者先执行try，全部成功后再执行confirm，否则try成功参与者执行cancel。 */
  public FlexibleTransactionManager registerTCC(
      @NotNull Runnable doTry, @NotNull Runnable doConfirm, @NotNull Runnable doCancel) {
    runnables.add(
        () -> {
          doTry.run();
          TransactionSynchronizationManager.registerSynchronization(
              new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                  if (status == STATUS_COMMITTED) {
                    try {
                      doConfirm.run();
                    } catch (Exception e) {
                      log.error("confirm failed!", e);
                    }
                  } else {
                    try {
                      doCancel.run();
                    } catch (Exception e) {
                      log.error("cancel failed!", e);
                    }
                  }
                }
              });
        });
    return this;
  }

  /** Saga柔性事务，所有参与者执行提交操作，如未全部成功，支持向前补偿（重试）和向后补偿（回滚）。 */
  public FlexibleTransactionManager registerSaga(
      @NotNull Runnable doCommit, @NotNull Runnable doRollback) {
    runnables.add(
        () -> {
          doCommit.run();
          TransactionSynchronizationManager.registerSynchronization(
              new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                  if (status != STATUS_COMMITTED) {
                    try {
                      doRollback.run();
                    } catch (Exception e) {
                      log.error("rollback failed!", e);
                    }
                  }
                }
              });
        });
    return this;
  }

  public void execute() {
    if (runnables.isEmpty()) {
      return;
    }
    if (!TransactionSynchronizationManager.isSynchronizationActive()) {
      throw new IllegalCallerException("Transaction synchronization is not active.");
    }
    runnables.forEach(Runnable::run);
  }
}
