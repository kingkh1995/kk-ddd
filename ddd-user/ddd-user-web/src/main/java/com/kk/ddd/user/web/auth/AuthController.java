package com.kk.ddd.user.web.auth;

import com.kk.ddd.support.annotation.BaseController;
import com.kk.ddd.support.model.command.AuthCommand;
import com.kk.ddd.support.model.command.PasswordModifyCommand;
import com.kk.ddd.support.model.group.Outer;
import java.nio.charset.StandardCharsets;
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
  public void login(@RequestBody @Validated AuthCommand authCommand) {
    authCommand.setCredential(decode(authCommand.getCredential()));
    authManager.login(authCommand);
  }

  /** Patch 部分更新资源 （幂等但url不能被缓存） */
  @PatchMapping("/auth/password")
  public void changePassword(@RequestBody @Validated(Outer.class) PasswordModifyCommand passwordModifyCommand) {
    passwordModifyCommand.setPlaintextPassword(decode(passwordModifyCommand.getEncodedPassword()));
    authManager.changePassword(passwordModifyCommand);
  }

  // 凭证的解码应该是在接口层，故代码应该在Controller中。
  private String decode(String encoded) {
    return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
  }
}
