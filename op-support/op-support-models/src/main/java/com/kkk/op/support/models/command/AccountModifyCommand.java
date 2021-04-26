package com.kkk.op.support.models.command;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 将ID等不允许被修改但需要提供的参数放在path上
 * 使用分组校验
 * @author KaiKoo
 */
@Data
public class AccountModifyCommand implements Serializable {

    @NotBlank(message = "test0不能为空！", groups = Update.class)
    private String test0;

    @NotBlank(message = "test1不能为空！", groups = {Create.class, Update.class})
    private String test1;

    public interface Update {

    }

    public interface Create {

    }
}
