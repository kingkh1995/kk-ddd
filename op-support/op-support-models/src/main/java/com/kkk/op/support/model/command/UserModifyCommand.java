package com.kkk.op.support.model.command;

import com.kkk.op.support.model.group.Update;
import java.io.Serializable;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.ConvertGroup;
import javax.validation.groups.Default;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * todo... 待完善
 *
 * @author KaiKoo
 */
@Data
@Accessors(chain = true)
public class UserModifyCommand implements Serializable {

  // 声明注解校验集合内对象
  // update情况下分组转为Default，因为此时Account可以是新增或更新，而create情况下只能是新增
  private List<
          @ConvertGroup(from = Update.class, to = Default.class) // 分组转换
          @NotNull @Valid AccountModifyCommand>
      accountModifyCommandList;
}
