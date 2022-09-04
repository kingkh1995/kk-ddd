package com.kk.ddd.support.bean;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 请求的上下文信息，需要能在整个请求过程（包括子线程）中被获取。 <br>
 * todo... 待设计
 *
 * @author KaiKoo
 */
@ToString
@Getter
public class LocalRequestContext {

  /** 日志链路追踪序号 */
  private final String traceId;

  /** 请求时间戳（请求处理过程中作为当前时间） */
  private final Instant timestamp;

  /** 时区信息 */
  private final ZoneId zoneId;

  /** 请求序列号（用于幂等处理，未传则默认为traceId） */
  private final String requestSeq;

  /** 调用程序入口：(method)uri */
  private final String entrance;

  /** 请求应用来源 */
  private final String source;

  @Builder // 可注解在构造方法和静态方法上
  public LocalRequestContext(
      String traceId,
      Instant timestamp,
      ZoneId zoneId,
      String requestSeq,
      String entrance,
      String source) {
    this.traceId = Objects.requireNonNullElse(traceId, UUID.randomUUID().toString());
    this.timestamp = Objects.requireNonNullElse(timestamp, Instant.now());
    this.zoneId = Objects.requireNonNullElse(zoneId, ZoneId.systemDefault());
    this.requestSeq = Objects.requireNonNullElse(requestSeq, this.traceId);
    this.entrance = entrance;
    this.source = source;
  }

  public ZonedDateTime getCommitTime() {
    return this.timestamp.atZone(this.zoneId);
  }

  public long calculateCostMillis() {
    return System.currentTimeMillis() - this.timestamp.toEpochMilli();
  }

  // 以下为登录信息，通过setter设置。

  /** 操作人用户ID */
  @Setter private Long operatorId;

  /** jwt claims */
  @Setter private Map<String, String> claims;
}
