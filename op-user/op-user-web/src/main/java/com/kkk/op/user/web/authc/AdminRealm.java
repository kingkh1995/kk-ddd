package com.kkk.op.user.web.authc;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.realm.SimpleAccountRealm;

/**
 * 配置管理员登录验证 <br>
 *
 * @author KaiKoo
 */
@Slf4j
public class AdminRealm extends SimpleAccountRealm {

  public AdminRealm(final List<String> admins) {
    super("AdminRealm");
    log.info("admins = {}", admins);
    admins.forEach(
        s -> {
          var split = s.split(":");
          super.addAccount(split[0], split[1]);
        });
  }
}
