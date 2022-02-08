package com.kkk.op.support.model.command;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Data
@Accessors(chain = true)
public class JobAddCommand implements Serializable {

  @NotBlank(message = "bizType不能为空！")
  private String bizType;

  @NotNull(message = "actionTime不能为空")
  private Long actionTime;

  @NotBlank(message = "context不能为空！")
  private String context;
}
