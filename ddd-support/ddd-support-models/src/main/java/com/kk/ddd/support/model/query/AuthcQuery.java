package com.kk.ddd.support.model.query;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Data
@Accessors(chain = true)
public class AuthcQuery implements Serializable {

  @NotBlank private String username;

  @NotBlank private String realmName;
}
