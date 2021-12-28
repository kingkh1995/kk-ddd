package com.kkk.op.support.shiro;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Data
@ConfigurationProperties("shiro.jwt")
public class JWTShiroProperties {

  private String tokenHeader = "Authorization";

  private String secretKey = "a2trLW9wLXVzZXItc2VjcmV0LWtleQ==";

  private String issuer = "kkk-op-user";

  private Long expiredAfterMinutes = 30L;
}
