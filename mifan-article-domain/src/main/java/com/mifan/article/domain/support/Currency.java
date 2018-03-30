package com.mifan.article.domain.support;

import java.util.HashMap;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/9/1
 */
public enum Currency {

    CNY("CNY", "¥"),
    EUR("EUR", "€"),
    GBP("GBP", "£"),
    USD("USD", "$");

    private static Map<String, Currency> map = new HashMap<>();

    static {
        for (Currency currency : Currency.values()) {
            map.put(currency.code, currency);
            map.put(currency.getSign(), currency);
        }
    }

    public static Currency from(String code) {
        return map.get(code);
    }

    private final String code;
    private final String sign;

    Currency(String code, String sign) {
        this.code = code;
        this.sign = sign;
    }

    public String getCode() {
        return code;
    }

    public String getSign() {
        return sign;
    }
}
