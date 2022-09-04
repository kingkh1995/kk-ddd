package com.kk.ddd.support.model.command;

import com.kk.ddd.support.enums.UserAuthTypeEnum;
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
public class AuthCommand implements Serializable {

  @NotNull
  private UserAuthTypeEnum authType;

  @NotBlank
  private String principal;

  @NotBlank
  private String credential;
}
