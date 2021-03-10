package com.kkk.op.user.web.controller;

import com.kkk.op.support.models.dto.AccountDTO;
import com.kkk.op.user.application.service.AccountAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author KaiKoo
 */
@Slf4j
@RequestMapping("/account")
@RestController
public class AccountController {

    @Autowired
    private AccountAppService service;

    @GetMapping("/{id}")
    public AccountDTO findById(@PathVariable Long id) {
        log.info("find by id:{}", id);
        return service.find(id);
    }

    @DeleteMapping("/")
    public void remove(@RequestBody Long id) {
        log.info("remove by id:{}", id);
        service.remove(id);
    }

    @PostMapping("/")
    public Long save(@RequestBody @Validated AccountDTO dto) {
        log.info("save dto:{}", dto);
        return service.save(dto);
    }
}
