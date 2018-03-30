package com.mifan.article.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/2/17
 */
public final class PinyinUtils {

    private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

    static {
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    public static String group(String s) {
        if (StringUtils.isEmpty(s)) {
            return "";
        }
        char c = s.charAt(0);
        if (isChinese(c)) {
            StringBuilder sb = new StringBuilder();
            pinyin(sb, c, null);
            if (sb.length() == 0) {
                return "#";
            } else {
                return sb.substring(0, 1);
            }
        } else {
            return hash(c);
        }
    }

    public static void pinyin(StringBuilder sb, String s) {
        Assert.notNull(s, "");
        for (char c : s.trim().toCharArray()) {
            pinyin(sb, c, " ");
        }
    }

    public static void pinyin(StringBuilder sb, char c, String suffix) {
        try {
            append(sb, c, suffix);
        } catch (Exception ignore) {
        }
    }

    public static String hash(char c) {
        int n = c;
        if ((65 <= n && n <= 90) || (65 <= (n -= 32) && n <= 90)) {
            return String.valueOf((char) n);
        } else {
            return "#";
        }
    }

    /**
     * <p>判断是否是中文</p>
     *
     * @param c char
     * @return true if is chinese code
     */
    public static boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;
    }

    /**
     * <p>判断是否是中文</p>
     *
     * @param str string
     * @return true if is chinese code
     */
    public static boolean isChinese(String str) {
        if (str == null) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    private static void append(StringBuilder sb, char c, String suffix) throws BadHanyuPinyinOutputFormatCombination {
        if (isChinese(c)) {
            String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, format);
            if (temp == null) {
                return;
            }

            sb.append(temp[0]);
            if (suffix != null) {
                sb.append(suffix);
            }
        } else {
            sb.append(c);
        }
    }

}
