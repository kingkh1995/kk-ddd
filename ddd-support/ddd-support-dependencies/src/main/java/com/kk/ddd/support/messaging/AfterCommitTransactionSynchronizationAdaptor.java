package com.kk.ddd.support.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.support.TransactionSynchronization;

/**
 * 本地事务提交后发送异步消息 <br>
 *
 * @author KaiKoo
 */
@RequiredArgsConstructor
public class AfterCommitTransactionSynchronizationAdaptor implements TransactionSynchronization {

  private final Runnable afterCommit;

  @Override
  public void afterCommit() {
    afterCommit.run();
  }
}
