package com.kkk.op.user.web.controller;

import com.kkk.op.support.models.command.CreateGroup;
import com.kkk.op.support.models.command.UpdateGroup;
import com.kkk.op.support.models.command.UserModifyCommand;
import com.kkk.op.user.domain.service.UserService;
import javax.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED) //201
    public long createUser(
            @RequestBody @Validated(CreateGroup.class) UserModifyCommand createCommand) {
        log.info("user create command：{}", createCommand);
        //todo... 实现
        return 0;
    }

    @PutMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED) //202
    public long updateUser(@PathVariable @Min(value = 1, message = "userId必须大于0！") String userId,
            @RequestBody @Validated(UpdateGroup.class) UserModifyCommand updateCommand) {
        log.info("userId：{}，user update command：{}", userId, updateCommand);
        //todo... 实现
        return 0;
    }

}
