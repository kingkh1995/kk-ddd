package com.kk.ddd.support.model.command;

import com.kk.ddd.support.model.group.Update;
import java.io.Serializable;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.ConvertGroup;
import javax.validation.groups.Default;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 将不允许被修改但需要提供的参数（如ID）放在path上 <br/>
 * todo... 待完善
 *
 * @author KaiKoo
 */
@Data
@Accessors(chain = true)  // chain模式不适用于继承的场景
public class UserModifyCommand implements Serializable {

  // 声明注解校验集合内对象
  // Update情况下分组转为Default，因为此时Account可以是新增或更新，而Create情况下只能是新增
  private List<
          @ConvertGroup(from = Update.class, to = Default.class) // 分组转换
          @NotNull @Valid AccountModifyCommand>
      accountModifyCommandList;
}
