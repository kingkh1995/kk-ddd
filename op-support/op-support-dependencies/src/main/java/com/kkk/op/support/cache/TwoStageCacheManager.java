package com.kkk.op.support.cache;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import org.springframework.cache.CacheManager;

/**
 * 二级缓存Manager <br>
 *
 * @author KaiKoo
 */
public abstract class TwoStageCacheManager implements CacheManager {

  public abstract void publishMessage(TwoStageCacheEvictMessage message);

  @JsonSerialize(typing = JsonSerialize.Typing.DYNAMIC) // 设置动态序列化，输出具体类型。
  @JsonDeserialize(builder = TwoStageCacheEvictMessage.TwoStageCacheEvictMessageBuilder.class)
  @Getter
  @Builder
  public static class TwoStageCacheEvictMessage implements Serializable {
    private String name;
    private Object key;
  }
}
