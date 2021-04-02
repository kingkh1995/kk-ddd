package com.kkk.op.support.tools;

import java.io.Reader;
import java.util.Arrays;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * KMP算法查找字符串
 * fixme... 有bug，待实现
 * @author KaiKoo
 */
public class KMP {

    /**
     * 辅助数组
     */
    private int[] auxs;

    private char[] chars;

    private KMP(char[] chars) {
        this.chars = chars;
        // 构造辅助数组
        var len = this.chars.length;
        this.auxs = new int[len];
        this.auxs[0] = -1;
        var k = -1;
        var j = 0;
        while (j < len - 1) {
            if (k == -1 || this.chars[j] == this.chars[k]) {
                k++;
                j++;
                this.auxs[j] = k;
            } else {
                k = this.auxs[k];
            }
        }
    }

    public static KMP compile(@NotBlank String template) {
        return compile0(Objects.requireNonNull(template).toCharArray());
    }

    public static KMP compile(@NotEmpty char[] chars) {
        return compile0(Arrays.copyOf(Objects.requireNonNull(chars), chars.length));
    }

    private static KMP compile0(char[] chars) {
        if (chars == null || chars.length == 0) {
            throw new IllegalArgumentException("chars can not be empty!");
        }
        return new KMP(chars);
    }

    public int indexOf(String str) {
        if (str == null || str.isBlank()) {
            return -1;
        }
        var len = this.chars.length;
        var sLen = str.length();
        var i = 0;
        var j = 0;
        while (i < sLen && j < len) {
            if (j == -1 || str.charAt(i) == this.chars[j]) {
                i++;
                j++;
                if (j == len) {
                    return i - j;
                }
            } else {
                j = this.auxs[j];
            }
        }
        return -1;
    }

    // todo... 从字符流中读取，并查找出匹配的所有index
    public long[] search(Reader reader) {
        return null;
    }
}
