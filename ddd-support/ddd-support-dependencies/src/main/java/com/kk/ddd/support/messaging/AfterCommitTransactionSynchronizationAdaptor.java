package com.kk.ddd.support.messaging;

import lombok.AllArgsConstructor;
import org.springframework.transaction.support.TransactionSynchronization;

/**
 * 本地事务提交后发送异步消息
 * <br/>
 *
 * @author KaiKoo
 */
@AllArgsConstructor
public class AfterCommitTransactionSynchronizationAdaptor implements TransactionSynchronization {

    private final Runnable afterCommit;

    @Override
    public void afterCommit() {
        afterCommit.run();
    }
}
