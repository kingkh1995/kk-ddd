package com.kkk.op.user.web.controller;

import com.kkk.op.support.models.command.AccountCreateCommand;
import com.kkk.op.support.models.command.AccountUpdateCommand;
import com.kkk.op.support.models.dto.AccountDTO;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.application.service.AccountApplicationService;
import java.util.List;
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
 * 对外接口层
 * todo...鉴权限流等
 * @author KaiKoo
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class AccountController {

    @Autowired
    private AccountApplicationService service;

    /**
     * POST 新增资源
     * @param createCommand
     * @return
     */
    @PostMapping("/user/{userId}/account")
    @ResponseStatus(HttpStatus.CREATED) //201
    public long create(@PathVariable Long userId,
            @RequestBody @Validated AccountCreateCommand createCommand) {
        log.info("userId：{}，account create command：{}", userId, createCommand);
        return service.createAccount(LongId.valueOf(userId), createCommand);
    }

    /**
     * PUT 全量更新资源
     * @param updateCommand
     * @return
     */
    @PutMapping("/user/{userId}/account/{accountId}")
    @ResponseStatus(HttpStatus.ACCEPTED) //202
    public void update(@PathVariable Long userId, @PathVariable Long accountId,
            @RequestBody @Validated AccountUpdateCommand updateCommand) {
        log.info("userId：{}，accountId：{}，account update command：{}", userId, accountId,
                updateCommand);
        service.updateAccount(LongId.valueOf(userId), LongId.valueOf(accountId), updateCommand);
    }

    /**
     * Delete 删除资源
     * @param accountId
     */
    @DeleteMapping("/user/{userId}/account/{accountId}")
    @ResponseStatus(HttpStatus.ACCEPTED) //202
    public void delete(@PathVariable Long userId, @PathVariable Long accountId) {
        log.info("userId：{}，delete account by accountId；{}", userId, accountId);
        service.deleteAccount(accountId);
    }

    /**
     * GET 获取资源
     * @param accountId
     * @return
     */
    @GetMapping("/user/{userId}/account/{accountId}")
    @ResponseStatus(HttpStatus.OK) //200
    public AccountDTO queryById(@PathVariable Long userId, @PathVariable Long accountId) {
        log.info("userId：{}，query account by accountId；{}", userId, accountId);
        return service.queryAccount(accountId);
    }

    /**
     * 查询用户下的所有账号
     * @param userId
     * @return
     */
    @GetMapping("/user/{userId}/accounts")
    public List<AccountDTO> queryByUserId(@PathVariable Long userId) {
        log.info("query accounts by userId:{}", userId);
        return service.queryAccountsByUserId(userId);
    }

}
