package com.kkk.op.user.web.controller;

import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.domain.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * todo...
 * @author KaiKoo
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}")
    public User findById(@PathVariable Long id) {
        log.info("find by id:{}", id);
        return userService.find(LongId.valueOf(id));
    }

}
