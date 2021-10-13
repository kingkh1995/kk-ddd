package com.kkk.op.support.model.command;

import java.io.Serializable;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * todo... 待完善
 *
 * @author KaiKoo
 */
@Data
public class UserModifyCommand implements Serializable {

  // 声明注解校验集合内对象
  private List<@NotNull @Valid AccountModifyCommand> accountModifyCommandList;
}
