package com.kk.ddd.support.model.event;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Data
@Accessors(chain = true)
public class JobActionEvent implements Serializable {

  @NotNull @PositiveOrZero private Integer slot;
}
