package com.kkk.op.support.base;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;

/**
 * 请求的上下文信息，需要能在整个请求过程（包括子线程）中被获取。 <br>
 * todo... 待设计
 *
 * @author KaiKoo
 */
@ToString
@Builder
public class LocalRequestContext {

  /**
   * 日志链路追踪序号
   *
   * <p>todo... 日志打印自动添加traceId
   */
  @Getter @Default private final String traceId = UUID.randomUUID().toString();

  /** 请求时间戳（请求处理过程中作为当前时间） */
  @Default private final Instant timestamp = Instant.now();

  /** 时区信息 */
  @Getter @Default private final ZoneId zoneId = ZoneId.systemDefault();

  /** 调用程序入口：(method)uri */
  @Getter private final String entrance;

  /** 请求应用来源 */
  @Getter private final String source;

  /** 请求序列号（用于幂等处理，未传则默认为traceId） */
  private final String requestSeq;

  /** 操作人用户ID */
  @Getter private final Long operatorId;

  /** jwt payload map */
  private Map<String, Object> payload;

  public ZonedDateTime getCommitTime() {
    return this.timestamp.atZone(this.zoneId);
  }

  public long calculateCostMillis() {
    return System.currentTimeMillis() - this.timestamp.toEpochMilli();
  }

  public String getRequestSeq() {
    return Optional.ofNullable(this.requestSeq).orElse(this.traceId);
  }
}
