package com.kkk.op.user.web.controller;

import com.kkk.op.support.models.command.AccountModifyCommand;
import com.kkk.op.support.models.command.CreateGroup;
import com.kkk.op.support.models.command.UpdateGroup;
import com.kkk.op.support.models.dto.AccountDTO;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.application.service.AccountApplicationService;
import com.kkk.op.user.domain.types.AccountId;
import java.util.List;
import javax.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外接口层 <br>
 * todo...鉴权限流等
 *
 * @author KaiKoo
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@Validated // 校验 @PathVariable @RequestParam
public class AccountController {

  @Autowired private AccountApplicationService service;

  /** POST 新增资源 */
  @PostMapping("/user/{userId}/account")
  @ResponseStatus(HttpStatus.CREATED) // 201
  public long create(
      @PathVariable @Min(value = 1, message = "userId必须大于0！") String userId,
      @RequestBody @Validated(CreateGroup.class) AccountModifyCommand createCommand) {
    log.info("userId：{}，account create command：{}", userId, createCommand);
    return service.createAccount(LongId.valueOf(userId, "userId"), createCommand);
  }

  /** PUT 全量更新资源 */
  @PutMapping("/user/{userId}/account/{accountId}")
  @ResponseStatus(HttpStatus.ACCEPTED) // 202
  public void update(
      @PathVariable @Min(value = 1, message = "userId必须大于0！") String userId,
      @PathVariable @Min(value = 1, message = "accountId必须大于0！") String accountId,
      @RequestBody @Validated(UpdateGroup.class) AccountModifyCommand updateCommand) {
    log.info("userId：{}，accountId：{}，account update command：{}", userId, accountId, updateCommand);
    service.updateAccount(
        LongId.valueOf(userId, "userId"), AccountId.from(accountId), updateCommand);
  }

  /** Delete 删除资源 */
  @DeleteMapping("/user/{userId}/account/{accountId}")
  @ResponseStatus(HttpStatus.NO_CONTENT) // 204
  public void delete(
      @PathVariable @Min(value = 1, message = "userId必须大于0！") String userId,
      @PathVariable @Min(value = 1, message = "accountId必须大于0！") String accountId) {
    log.info("userId：{}，delete account by accountId；{}", userId, accountId);
    service.deleteAccount(AccountId.from(accountId));
  }

  /** GET 获取资源 */
  @GetMapping("/user/{userId}/account/{accountId}")
  @ResponseStatus(HttpStatus.OK) // 200
  public AccountDTO queryById(
      @PathVariable @Min(value = 1, message = "userId必须大于0！") String userId,
      @PathVariable @Min(value = 1, message = "accountId必须大于0！") String accountId) {
    log.info("userId：{}，query account by accountId；{}", userId, accountId);
    return service.queryAccount(AccountId.from(accountId));
  }

  /** 查询用户下的所有账号 */
  @GetMapping("/user/{userId}/accounts")
  public List<AccountDTO> queryByUserId(@PathVariable String userId) {
    log.info("query accounts by userId:{}", userId);
    return service.queryAccounts(LongId.valueOf(userId, "userId"));
  }
}
