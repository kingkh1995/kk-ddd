package com.kkk.op.support.models.user;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author KaiKoo
 */
@Data
public class AccountDTO implements Serializable {

    private Long id;

    @NotNull
    private Long userId;

    private String status;

    // 日期类默认转换为时间戳
    private Long createTime;

}
