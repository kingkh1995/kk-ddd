package com.kk.ddd.support.model.event;

import java.io.Serializable;
import java.util.Date;
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
public class JobReverseEvent implements Serializable {

  @NotNull @Positive private Long id;

  @NotNull @Positive private Date actionTime;
}
