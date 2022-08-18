package com.kk.ddd.support.messaging;

import java.util.List;
import java.util.concurrent.ExecutorService;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;

/**
 * 消息持久化
 *
 * <br/>
 *
 * @author KaiKoo
 */
public interface MessageRecorder {

    List<MessageModel> record(String topic, String hashKey, List<Message<?>> messages);

    void succeed(Long modelId);

    ExecutorService callbackExecutor();

    MessageConverter messageConverter();

}
