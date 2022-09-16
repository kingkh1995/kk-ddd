package com.kk.ddd.support.messaging;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import lombok.Data;
import org.springframework.messaging.Message;

/**
 *
 * <br/>
 *
 * @author KaiKoo
 */
@Data
public class MessageModel implements Serializable {
    private Long id;
    private Long formerId;
    private String topic;
    private String hashKey;
    private Long createTime;
    private Long sendTime;
    private String header;
    private String payload;
    private transient Message<?> message;
    private transient CompletableFuture<?> future;
}
