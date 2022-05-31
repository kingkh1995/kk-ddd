package com.kk.ddd.support.model.command;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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

  @NotBlank(message = "topic不能为空！")
  private String topic;

  @Positive
  @NotNull(message = "actionTime不能为空!")
  private Long actionTime;

  @NotBlank(message = "context不能为空！")
  private String context;
}
