package com.kk.ddd.support.model.command;

import com.kk.ddd.support.model.group.Create;
import com.kk.ddd.support.model.group.Update;
import java.io.Serializable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 *
 * @author KaiKoo
 */
@Data
@Accessors(chain = true)
public class AccountModifyCommand implements Serializable {

  @Null(message = "id必须为空！", groups = Create.class)
  @Null(message = "id不能为空！", groups = Update.class)
  private Long id;

  @NotNull
  @Min(value = 1, message = "userId需要大于${value - 1}！") // 使用${}表达式
  private Long userId;

  @NotBlank(message = "test0不能为空！", groups = Update.class)
  private String test0;

  @NotBlank(message = "test1不能为空！") // 默认校验分组，Create、Update均会生效
  private String test1;
}
