package com.kkk.op.support.model.command;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import lombok.Data;

/**
 * 将I不允许被修改但需要提供的参数（如ID）放在path上
 *
 * @author KaiKoo
 */
@Data
public class AccountModifyCommand implements Serializable {

  @Null(message = "id必须为空！", groups = CreateGroup.class)
  private Long id;

  @NotBlank(message = "test0不能为空！", groups = UpdateGroup.class)
  private String test0;

  @NotBlank(
      message = "test1不能为空！",
      groups = {CreateGroup.class, UpdateGroup.class})
  private String test1;
}
