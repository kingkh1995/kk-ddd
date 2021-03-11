package com.kkk.op.user.web.controller;

import com.kkk.op.support.models.user.AccountDTO;
import com.kkk.op.support.models.user.AccountQueryDTO;
import com.kkk.op.user.application.service.AccountAppService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author KaiKoo
 */
@Slf4j
@RestController
public class AccountController {

    @Autowired
    private AccountAppService service;

    @GetMapping("/account/{id}")
    public AccountDTO findById(@PathVariable Long id) {
        log.info("find by id:{}", id);
        return service.find(id);
    }

    @DeleteMapping("/account/{id}")
    public void remove(@PathVariable Long id) {
        log.info("remove by id:{}", id);
        service.remove(id);
    }

    @PostMapping("/account")
    public Long save(@RequestBody @Validated AccountDTO dto) {
        log.info("save dto:{}", dto);
        return service.save(dto);
    }

    @GetMapping("/accounts/{userId}")
    public List<AccountDTO> listByUserId(@PathVariable Long userId) {
        log.info("list by userId:{}", userId);
        var accountQueryDTO = new AccountQueryDTO();
        accountQueryDTO.setUserId(userId);
        return service.list(accountQueryDTO);
    }
}
