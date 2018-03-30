package com.mifan.article.es;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/24
 */
public class PinyinTests {

    private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

    static {
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    public static boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;
    }

    public static String a(String text) {
        try {
            StringBuilder sb = new StringBuilder();
            for (char c : text.toCharArray()) {
                if (isChinese(c)) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (temp != null && temp.length != 0) {
                        sb.append(temp[0].charAt(0));
                    }
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        String s = "中华人民共和国，你好, 我不知道, 女人hello";
        System.out.println(a(s));
    }

}
