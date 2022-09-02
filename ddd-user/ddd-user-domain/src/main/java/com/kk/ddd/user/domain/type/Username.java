package com.kk.ddd.user.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kk.ddd.support.util.ValidateUtils;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * <br/>
 *
 * @author KaiKoo
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Username implements Comparable<Username> {

    @Getter @JsonValue private final String value;

    // 只允许小写字母和下划线，长度为2-20
    public static final Pattern PATTERN = Pattern.compile("[a-z_]{2,20}");

    private static Username of(final String value, final String filedName) {
        ValidateUtils.matches(value, PATTERN, filedName);
        return new Username(value);
    }

    @JsonCreator
    public static Username of(final String value) {
        return of(value, "Username");
    }

    public static Username valueOf(final String value, final String filedName) {
        ValidateUtils.nonNull(value, filedName);
        return of(value, filedName);
    }

    @Override
    public int compareTo(Username o) {
        return this.value.compareTo(o.value);
    }
}
