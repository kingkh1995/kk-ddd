package com.kkk.op.user.web.controller;

import com.kkk.op.support.models.command.CreateGroup;
import com.kkk.op.support.models.command.UserModifyCommand;
import com.kkk.op.user.domain.service.UserService;
import javax.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    /**
     * POST 新增资源
     */
    @PostMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED) //201
    public long create(@PathVariable @Min(value = 1, message = "userId必须大于0！") String userId,
            @RequestBody @Validated(CreateGroup.class) UserModifyCommand createCommand) {
        log.info("userId：{}，user create command：{}", userId, createCommand);
        //todo... 实现
        return 0;
    }

}
