package com.kkk.op.user.web.authc;

import com.kkk.op.support.annotation.BaseController;
import com.kkk.op.support.model.command.AuthcCommand;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@BaseController
@RequestMapping
public class AuthcController {

  private final AuthcService authcService;

  @PostMapping("/login")
  public void login(@RequestBody @Validated AuthcCommand authcCommand) {
    authcCommand.setPlaintextPassword(
        new String(Base64.getDecoder().decode(authcCommand.getEncodedPassword())));
    SecurityUtils.getSubject()
        .login(
            new UsernamePasswordToken(
                authcCommand.getUsername(), authcCommand.getPlaintextPassword()));
  }

  /** Patch 部分更新资源 （幂等但url不能被缓存） */
  @PatchMapping("/authc/password")
  public void savePasswrod(@RequestBody @Validated AuthcCommand authcCommand) {
    authcCommand.setPlaintextPassword(
        new String(Base64.getDecoder().decode(authcCommand.getEncodedPassword())));
    authcService.savePassword(authcCommand);
  }
}