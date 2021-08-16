package com.kkk.op.support.bean;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

/**
 * http请求上下文信息 <br>
 * todo... 待设计
 *
 * @author KaiKoo
 */
@Getter
@Builder
public class BaseRequestContext {

  /** 日志链路追踪序号 */
  @Default private String traceId = UUID.randomUUID().toString().replace("-", "");

  /** 请求时间戳（带时区信息）（执行过程中作为当前时间） */
  @Default private ZonedDateTime zonedTimestamp = ZonedDateTime.now();

  /** 调用程序入口：(method)uri */
  private String entrance;

  /** 请求应用来源 */
  private String source;

  /** 请求序列号（用于幂等处理） */
  private String requestSeq;

  /** 操作人用户ID */
  private Long operatorId;

  /** accessCondition记录 */
  private String accessCondition;

  /** jwt payload map */
  private Map<String, Object> payload;

  public long calculateCostMillis() {
    return System.currentTimeMillis() - this.zonedTimestamp.toInstant().toEpochMilli();
  }

  public void recordAccessCondition(String accessCondition) {
    // 只允许记录一次
    if (this.accessCondition == null) {
      this.accessCondition = accessCondition;
    }
  }
}
