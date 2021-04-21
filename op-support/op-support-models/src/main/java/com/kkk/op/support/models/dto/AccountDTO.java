package com.kkk.op.support.models.dto;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author KaiKoo
 */
@Data
public class AccountDTO implements Serializable {

    private Long id;

    private Long userId;

    private String status;

    // 日期类默认转换为时间戳
    private Long createTime;

}
