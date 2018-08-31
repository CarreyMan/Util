package util;

import java.util.regex.Pattern;

public class PatternUtil {

    // 非中文任意字符
    private static final String ANY_BUT_CHINESE = "[^\\u4e00-\\u9fa5]";

    // 任意ASCII字符
    private static final String ANY_ASCII = "\\p{ASCII}";

    // 任意数字
    private static final String ANY_DIGIT = "\\p{Digit}";

    // 任意ALPHA字符不区分大小写
    private static final String ANY_ALPHA = "\\p{Alpha}";

    // 任意ALPHA小写字符
    private static final String ANY_LOWER_ALPHA = "\\p{Lower}";

    // 任意ALPHA大写字符
    private static final String ANY_UPPER_ALPHA = "\\p{Upper}";

    // 任意字符不区分大小写
    private static final String ANY_ALPHA_DIGIT = "[\\p{Alpha}\\p{Digit}]";

    private PatternUtil() {}

    /** 非中文任意字符 **/
    public static boolean matchAnyButChinese(String s) {
        return matchAnyButChinese(s, 1);
    }

    /** 非中文任意字符,至少min个字符 **/
    public static boolean matchAnyButChinese(String s, int min) {
        return Pattern.matches(String.format("%s{%d,}", ANY_BUT_CHINESE, min), s);
    }

    /** 非中文任意字符,min-max个字符 **/
    public static boolean matchAnyButChinese(String s, int min, int max) {
        return Pattern.matches(String.format("%s{%d,%d}", ANY_BUT_CHINESE, min, max), s);
    }

    /** 任意ASCII字符 **/
    public static boolean matchAnyASCII(String s) {
        return matchAnyASCII(s, 1);
    }

    /** 任意ASCII字符,至少min个字符 **/
    public static boolean matchAnyASCII(String s, int min) {
        return Pattern.matches(String.format("%s{%d,}", ANY_ASCII, min), s);
    }

    /** 任意ASCII字符,min-max个字符 **/
    public static boolean matchAnyASCII(String s, int min, int max) {
        return Pattern.matches(String.format("%s{%d,%d}", ANY_ASCII, min, max), s);
    }

    /** 任意数字 **/
    public static boolean matchAnyDigit(String s) {
        return matchAnyDigit(s, 1);
    }

    /** 任意数字,至少min个字符 **/
    public static boolean matchAnyDigit(String s, int min) {
        return Pattern.matches(String.format("%s{%d,}", ANY_DIGIT, min), s);
    }

    /** 任意数字,min-max个字符 **/
    public static boolean matchAnyDigit(String s, int min, int max) {
        return Pattern.matches(String.format("%s{%d,%d}", ANY_DIGIT, min, max), s);
    }

    /** 任意alpha字符 **/
    public static boolean matchAnyAlpha(String s) {
        return matchAnyAlpha(s, 1);
    }

    /** 任意alpha字符,至少min个字符 **/
    public static boolean matchAnyAlpha(String s, int min) {
        return Pattern.matches(String.format("%s{%d,}", ANY_ALPHA, min), s);
    }

    /** 任意alpha字符,min-max个字符 **/
    public static boolean matchAnyAlpha(String s, int min, int max) {
        return Pattern.matches(String.format("%s{%d,%d}", ANY_ALPHA, min, max), s);
    }

    /** 任意alpha和数字字符 **/
    public static boolean matchAnyAlphaDigit(String s) {
        return matchAnyAlphaDigit(s, 1);
    }

    /** 任意alpha和数字字符,至少min个字符 **/
    public static boolean matchAnyAlphaDigit(String s, int min) {
        return Pattern.matches(String.format("%s{%d,}", ANY_ALPHA_DIGIT, min), s);
    }

    /** 任意alpha和数字字符,min-max个字符 **/
    public static boolean matchAnyAlphaDigit(String s, int min, int max) {
        return Pattern.matches(String.format("%s{%d,%d}", ANY_ALPHA_DIGIT, min, max), s);
    }

}