package com.kk.ddd.job.message;

import java.util.function.BooleanSupplier;

/**
 * 发送任务执行消息，涉及到分布式事务问题，解决方案包括：使用事务消息、保证任务执行的幂等性等等。 <br>
 *
 * @author KaiKoo
 */
public interface JobMessageSender {

  /**
   * 发送任务执行消息，并设置本地操作，如果失败则不会执行操作。
   *
   * @param topic 消息topic
   * @param context 消息内容
   * @param localAction 发送成功后本地执行操作
   */
  void send(String topic, String context, BooleanSupplier localAction);
}
