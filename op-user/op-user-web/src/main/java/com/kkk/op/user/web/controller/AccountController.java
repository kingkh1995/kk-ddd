package com.kkk.op.user.web.controller;

import com.kkk.op.support.annotations.BaseController;
import com.kkk.op.support.bean.Result;
import com.kkk.op.support.models.command.AccountModifyCommand;
import com.kkk.op.support.models.command.CreateGroup;
import com.kkk.op.support.models.command.UpdateGroup;
import com.kkk.op.support.models.dto.AccountDTO;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.application.service.AccountApplicationService;
import com.kkk.op.user.domain.types.AccountId;
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
  public Result<Long> create(
      @PathVariable @Min(value = 1, message = "userId必须大于0！") String userId,
      @RequestBody @Validated(CreateGroup.class) AccountModifyCommand createCommand) {
    return Result.success(service.createAccount(LongId.valueOf(userId, "userId"), createCommand));
  }

  /** PUT 全量更新资源 */
  @PutMapping("/user/{userId}/account/{accountId}")
  @ResponseStatus(HttpStatus.ACCEPTED) // 202
  public Result<?> update(
      @PathVariable @Min(value = 1, message = "userId必须大于0！") String userId,
      @PathVariable @Min(value = 1, message = "accountId必须大于0！") String accountId,
      @RequestBody @Validated(UpdateGroup.class) AccountModifyCommand updateCommand) {
    service.updateAccount(
        LongId.valueOf(userId, "userId"), AccountId.valueOf(accountId), updateCommand);
    return Result.success();
  }

  /** Delete 删除资源 */
  @DeleteMapping("/user/{userId}/account/{accountId}")
  @ResponseStatus(HttpStatus.NO_CONTENT) // 204
  public Result<?> delete(
      @PathVariable @Min(value = 1, message = "userId必须大于0！") String userId,
      @PathVariable @Min(value = 1, message = "accountId必须大于0！") String accountId) {
    service.deleteAccount(AccountId.valueOf(accountId));
    return Result.success();
  }

  /** GET 获取资源 */
  @GetMapping("/user/{userId}/account/{accountId}")
  @ResponseStatus(HttpStatus.OK) // 200
  public Result<AccountDTO> queryById(
      @PathVariable @Min(value = 1, message = "userId必须大于0！") String userId,
      @PathVariable @Min(value = 1, message = "accountId必须大于0！") String accountId) {
    return Result.success(service.queryAccount(AccountId.valueOf(accountId)));
  }

  /** 查询用户下的所有账号 */
  @GetMapping("/user/{userId}/accounts")
  public Result<List<AccountDTO>> queryByUserId(@PathVariable String userId) {
    return Result.success(service.queryAccounts(LongId.valueOf(userId, "userId")));
  }
}
