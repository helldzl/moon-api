package com.mifan.user.type;

import org.moonframework.core.support.EnumSupporter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/4
 */
public enum AccountType implements EnumSupporter {

    MOBILE(0, "手机", AccountType.REGEXP_MOBILE),

    // 必须放在邮箱前面, 先验证比较具体的微信格式, 再验证比较泛化的邮箱格式, 因为二者是 is-a 关系
    WE_CHAT(3, "微信", AccountType.REGEXP_WE_CHAT),

    EMAIL(1, "邮箱", AccountType.REGEXP_EMAIL);

    // 手机或邮箱格式
    //private static final String REGEXP_USERNAME = "(^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$)|(^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$)";
    //private static final String REGEXP_USERNAME = "^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";
    //private static final String REGEXP_USERNAME = "(^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$)|(^([a-z0-9A-Z]+[-|\\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$)";
    public static final String REGEXP_MOBILE = "(^1(3[0-9]|4[579]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$)";
    public static final String REGEXP_EMAIL = "(^([a-z0-9A-Z]+[-|\\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$)";
//    public static final String REGEXP_WE_CHAT = "^([a-z0-9A-Z]+[-|\\.|_]?)+[a-z0-9A-Z]@(budeeweixin.com)$";
    public static final String REGEXP_WE_CHAT = "^[\\s\\S]+(@budeeweixin.com)$";
    public static final String REGEXP_USERNAME = REGEXP_MOBILE + "|" + REGEXP_EMAIL;

    private static Map<Integer, AccountType> map = new HashMap<>();

    static {
        for (AccountType e : values()) {
            map.put(e.getIndex(), e);
        }
    }

    public static AccountType fromIndex(int index) {
        return map.get(index);
    }

    private final int index;
    private final String name;
    private final String regexp;

    AccountType(int value, String name, String regexp) {
        this.index = value;
        this.name = name;
        this.regexp = regexp;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getRegexp() {
        return regexp;
    }
}
