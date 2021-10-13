package com.kkk.op.user.web.provider;

import com.kkk.op.support.model.command.UserModifyCommand;
import com.kkk.op.support.model.groups.Create;
import com.kkk.op.support.model.groups.Update;
import javax.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 接口层 参数校验只能由父类或接口定义 <br>
 *
 * @author KaiKoo
 */
@Validated // 校验 @PathVariable @RequestParam 需要添加 @Validated 注解
@RequestMapping("/api/v1/user")
public interface UserProvider {

  @PostMapping("")
  @ResponseStatus(HttpStatus.CREATED)
  long createUser(@RequestBody @Validated(Create.class) UserModifyCommand createCommand);

  @PutMapping("/{userId}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  long updateUser(
      @PathVariable @Positive(message = "userId必须为正数！") Long userId,
      @RequestBody @Validated(Update.class) UserModifyCommand updateCommand);
}