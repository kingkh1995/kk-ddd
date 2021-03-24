package com.kkk.op.support.enums;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    timpstamp(null), // 秒级时间戳
    yyyy_MM_dd_HH_mm_ss(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
    ;

    @Getter
    private DateTimeFormatter formatter;

    public LocalDateTime parse(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        text = text.strip();
        if (this.formatter == null) {
            Long epochSecond;
            try {
                epochSecond = Long.valueOf(text);
            } catch (NumberFormatException e) {
                // 转为DateTimeException
                throw new DateTimeException(e.getMessage(), e);
            }
            // 默认为当前时区
            return Instant.ofEpochSecond(epochSecond).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return LocalDateTime.parse(text, this.formatter);
    }

    public String format(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        }
        if (this.formatter == null) {
            // 默认为当前时区
            return String.valueOf(localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond());
        }
        return localDateTime.format(this.formatter);
    }


}
