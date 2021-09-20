package com.kkk.op.user.web.controller;

import com.kkk.op.support.annotation.BaseController;
import com.kkk.op.support.model.command.AccountModifyCommand;
import com.kkk.op.support.model.command.CreateGroup;
import com.kkk.op.support.model.command.UpdateGroup;
import com.kkk.op.support.model.dto.AccountDTO;
import com.kkk.op.user.application.service.AccountApplicationService;
import java.util.List;
import javax.validation.constraints.Min;
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
@RequiredArgsConstructor
@BaseController
@RequestMapping("/api/v1")
public class AccountController {

  private final AccountApplicationService service;

  /** POST 新增资源 */
  @PostMapping("/user/{userId}/account")
  @ResponseStatus(HttpStatus.CREATED) // 201
  public long create(
      @PathVariable @Min(value = 1, message = "userId必须大于0！") Long userId,
      @RequestBody @Validated(CreateGroup.class) AccountModifyCommand createCommand) {
    createCommand.setUserId(userId);
    return service.createAccount(createCommand);
  }

  /** PUT 全量更新资源 */
  @PutMapping("/user/{userId}/account/{accountId}")
  @ResponseStatus(HttpStatus.ACCEPTED) // 202
  public boolean update(
      @PathVariable @Min(value = 1, message = "userId必须大于0！") Long userId,
      @PathVariable @Min(value = 1, message = "accountId必须大于0！") Long accountId,
      @RequestBody @Validated(UpdateGroup.class) AccountModifyCommand updateCommand) {
    updateCommand.setId(accountId).setUserId(userId);
    service.updateAccount(updateCommand);
    return true;
  }

  /** Delete 删除资源 */
  @DeleteMapping("/user/{userId}/account/{accountId}")
  @ResponseStatus(HttpStatus.ACCEPTED) // 202
  public boolean delete(
      @PathVariable @Min(value = 1, message = "userId必须大于0！") Long userId,
      @PathVariable @Min(value = 1, message = "accountId必须大于0！") Long accountId) {
    service.deleteAccount(userId, accountId);
    return true;
  }

  /** GET 获取资源 */
  @GetMapping("/user/{userId}/account/{accountId}")
  @ResponseStatus(HttpStatus.OK) // 200
  public AccountDTO queryById(
      @PathVariable @Min(value = 1, message = "userId必须大于0！") Long userId,
      @PathVariable @Min(value = 1, message = "accountId必须大于0！") Long accountId) {
    return service.queryAccount(userId, accountId);
  }

  /** 查询用户下的所有账号 */
  @GetMapping("/user/{userId}/accounts")
  public List<AccountDTO> queryByUserId(@PathVariable Long userId) {
    return service.queryAccounts(userId);
  }
}
