package com.kkk.op.support.shiro;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Data
@ConfigurationProperties("shiro")
public class ShiroProperties {

  protected String loginUrl = "/login";

  protected String successUrl = "/";

  protected String unauthorizedUrl = null;
}
