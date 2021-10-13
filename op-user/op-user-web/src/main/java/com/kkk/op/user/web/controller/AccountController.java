package com.kkk.op.user.web.controller;

import com.kkk.op.support.annotation.BaseController;
import com.kkk.op.support.model.command.AccountModifyCommand;
import com.kkk.op.support.model.dto.AccountDTO;
import com.kkk.op.support.model.groups.Create;
import com.kkk.op.support.model.groups.Update;
import com.kkk.op.user.application.service.AccountApplicationService;
import java.util.List;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 对外接口层 <br>
 * todo...鉴权限流等
 *
 * @author KaiKoo
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@BaseController
@RequestMapping("/api/v1/user")
public class AccountController {

  private final AccountApplicationService service;

  /** POST 新增资源 */
  @PostMapping("/{userId}/account")
  @ResponseStatus(HttpStatus.CREATED) // 201
  public long create(
      @PathVariable @Positive(message = "userId必须为正整数！") Long userId,
      @RequestBody @Validated(Create.class) AccountModifyCommand createCommand) {
    createCommand.setUserId(userId);
    return service.createAccount(createCommand);
  }

  /** PUT 全量更新资源 */
  @PutMapping("/{userId}/account/{accountId}")
  @ResponseStatus(HttpStatus.ACCEPTED) // 202
  public boolean update(
      @PathVariable @Positive(message = "userId必须为正整数！") Long userId,
      @PathVariable @Positive(message = "accountId必须为正整数！") Long accountId,
      @RequestBody @Validated(Update.class) AccountModifyCommand updateCommand) {
    updateCommand.setId(accountId).setUserId(userId);
    service.updateAccount(updateCommand);
    return true;
  }

  /** Delete 删除资源 */
  @DeleteMapping("/{userId}/account/{accountId}")
  @ResponseStatus(HttpStatus.ACCEPTED) // 202
  public boolean delete(
      @PathVariable @Positive(message = "userId必须为正整数！") Long userId,
      @PathVariable @Positive(message = "accountId必须为正整数！") Long accountId) {
    service.deleteAccount(userId, accountId);
    return true;
  }

  /** GET 获取资源 */
  @GetMapping("/{userId}/account/{accountId}")
  @ResponseStatus(HttpStatus.OK) // 200
  public AccountDTO queryById(
      @PathVariable @Positive(message = "userId必须为正整数！") Long userId,
      @PathVariable @Positive(message = "accountId必须为正整数！") Long accountId) {
    return service.queryAccount(userId, accountId);
  }

  /** 查询用户下的所有账号 */
  @GetMapping("/{userId}/accounts")
  public List<AccountDTO> queryByUserId(@PathVariable Long userId) {
    return service.queryAccounts(userId);
  }
}
