package com.kkk.op.support.model.command;

import java.io.Serializable;
import java.util.List;
import javax.validation.Valid;
import lombok.Data;
import lombok.experimental.Delegate;

/**
 * todo... 待完善
 *
 * @author KaiKoo
 */
@Data
public class UserModifyCommand implements Serializable {

  @Valid @Delegate // 声明注解校验集合内对象
  private List<AccountModifyCommand> accountModifyCommandList;
}
