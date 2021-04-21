package com.kkk.op.user.web.controller;

import com.kkk.op.support.models.command.AccountCreateCommand;
import com.kkk.op.support.models.command.AccountUpdateCommand;
import com.kkk.op.support.models.dto.AccountDTO;
import com.kkk.op.user.application.service.AccountApplicationService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author KaiKoo
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountApplicationService service;

    /**
     * create POST
     * @param createCommand
     * @return
     */
    @PostMapping("/account")
    public long create(@RequestBody @Validated AccountCreateCommand createCommand) {
        log.info("account create command:{}", createCommand);
        return service.createAccount(createCommand);
    }

    /**
     * update PUT
     * @param updateCommand
     * @return
     */
    @PutMapping("/account")
    public void update(@RequestBody @Validated AccountUpdateCommand updateCommand) {
        log.info("account update command:{}", updateCommand);
        service.updateAccount(updateCommand);
    }

    /**
     * Delete DELETE
     * @param id
     */
    @DeleteMapping("/account/{id}")
    public void delete(@PathVariable Long id) {
        log.info("delete account by id:{}", id);
        service.deleteAccount(id);
    }

    /**
     * query GET
     * @param id
     * @return
     */
    @GetMapping("/account/{id}")
    public AccountDTO queryById(@PathVariable Long id) {
        log.info("query account by id:{}", id);
        return service.queryAccountById(id);
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
