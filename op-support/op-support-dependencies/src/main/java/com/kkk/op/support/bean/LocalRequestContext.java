package com.kkk.op.support.bean;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;

/**
 * http请求上下文信息 <br>
 * todo... 待设计
 *
 * @author KaiKoo
 */
@Getter
@ToString
@Builder
public class LocalRequestContext {

  /** 日志链路追踪序号 */
  @Default
  private final String traceId = UUID.randomUUID().toString().replace("-", "").substring(16);

  /** 请求时间戳（请求处理过程中作为当前时间） */
  @Default private final Instant timestamp = Instant.now();

  /** 时区信息 */
  private ZoneId zoneId;

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
    return System.currentTimeMillis() - this.timestamp.toEpochMilli();
  }

  // 抓取accessCondition，不允许覆盖
  public void captureAccessCondition(String accessCondition) {
    if (this.accessCondition == null) {
      this.accessCondition = accessCondition;
    }
  }

  // 回放抓取的accessCondition，回放完清空，以便多次使用
  public String replayAccessCondition() {
    var accessCondition = this.accessCondition;
    this.accessCondition = null;
    return accessCondition;
  }
}
