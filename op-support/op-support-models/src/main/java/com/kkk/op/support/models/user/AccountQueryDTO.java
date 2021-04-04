package com.kkk.op.support.models.user;

import com.kkk.op.support.models.base.AbstractQueryDTO;
import lombok.Data;

/**
 *
 * @author KaiKoo
 */
@Data
public class AccountQueryDTO extends AbstractQueryDTO {

    private Long id;

    private Long userId;

    private String status;

    private Long[] ids;

    // 日期类默认为字符串，为格式化之后的值
    private String createTimeStart;

    // 日期类默认为字符串，为格式化之后的值
    private String createTimeEnd;

}
