package com.kk.ddd.user.web.auth;

import com.kk.ddd.support.annotation.BaseController;
import com.kk.ddd.support.model.command.AuthCommand;
import com.kk.ddd.support.model.group.Outer;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 认证控制器 <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@BaseController
@RequestMapping
public class AuthController {

  private final AuthManager authManager;

  @PostMapping("/login")
  public void login(@RequestBody @Validated(Outer.class) AuthCommand authCommand) {
    decodeCommand(authCommand);
    authManager.login(authCommand);
  }

  /** Patch 部分更新资源 （幂等但url不能被缓存） */
  @PatchMapping("/authc/password")
  public void changePassword(@RequestBody @Validated(Outer.class) AuthCommand authCommand) {
    decodeCommand(authCommand);
    authManager.changePassword(authCommand);
  }

  // 解码密码为明文，此处使用Base64编码。因为是由调用方解码，故代码应该在Controller中。
  private void decodeCommand(AuthCommand authCommand) {
    authCommand.setPlaintextPassword(
        new String(Base64.getDecoder().decode(authCommand.getEncodedPassword())));
  }
}
