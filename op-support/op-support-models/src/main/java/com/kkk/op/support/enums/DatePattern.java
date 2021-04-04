package com.kkk.op.support.enums;

import com.kkk.op.support.tools.DateUtil;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 日期格式枚举 默认当前时区
 *
 * @throws java.time.DateTimeException
 * @author KaiKoo
 */
@AllArgsConstructor
public enum DatePattern {

    epochSecond(null),
    yyyy_MM_dd_HH_mm_ss(DateUtil.yyyy_MM_dd_HH_mm_ss),
    ;

    @Getter
    private DateTimeFormatter formatter;

    public LocalDateTime parse(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        text = text.strip();
        if (this.formatter != null) {
            return LocalDateTime.parse(text, this.formatter);
        }
        Long epochSecond;
        try {
            epochSecond = Long.valueOf(text);
        } catch (NumberFormatException e) {
            // 转为DateTimeException
            throw new DateTimeException(e.getMessage(), e);
        }
        return DateUtil.toLocalDateTime(epochSecond);
    }

    public String format(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        }
        if (this.formatter != null) {
            return localDateTime.format(this.formatter);
        }
        return String.valueOf(DateUtil.toEpochSecond(localDateTime));
    }


}
