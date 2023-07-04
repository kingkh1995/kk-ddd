package com.kk.ddd.support.messaging;

import com.kk.ddd.support.bean.Jackson;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.Message;

/**
 * @see RDBStorageSQLMapper <br>
 * @author KaiKoo
 */
@RequiredArgsConstructor
public final class MessageStorage implements InitializingBean {

  private final MessageStorageSQLMapper messageStorageSQLMapper;

  public MessageStorage(final JdbcTemplate jdbcTemplate)
      throws SQLException, ReflectiveOperationException {
    this.messageStorageSQLMapper = new MessageStorageSQLMapper(jdbcTemplate);
  }

  public List<MessageModel> save(String topic, String hashKey, List<Message<?>> messages) {
    var models =
        messages.stream()
            .map(
                message -> {
                  var model = new MessageModel();
                  model.setMessage(message);
                  model.setTopic(topic);
                  model.setHashKey(hashKey);
                  model.setCreateTime(message.getHeaders().getTimestamp());
                  model.setHeader(Jackson.object2String(message.getHeaders()));
                  model.setPayload(Jackson.object2String(message.getPayload()));
                  return model;
                })
            .toList();
    models.stream()
        .reduce(
            (former, cur) -> {
              messageStorageSQLMapper.insert(former);
              cur.setFormerId(former.getId());
              return cur;
            })
        .ifPresent(messageStorageSQLMapper::insert);
    return models;
  }

  public void complete(Long modelId) {
    messageStorageSQLMapper.updateForComplete(modelId);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    this.messageStorageSQLMapper.createTableIfNotExisted();
  }
}
