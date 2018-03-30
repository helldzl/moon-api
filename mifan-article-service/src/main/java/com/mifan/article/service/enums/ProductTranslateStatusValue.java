package com.mifan.article.service.enums;

/**
 * Created by LiuKai on 2017/1/18.
 */
public enum ProductTranslateStatusValue {
    INITIAL(0, "初始状态"),
    GOOGLE(1, "谷歌翻译"),
    USER(2, "用户翻译");
    private Integer code;
    private String name;

    ProductTranslateStatusValue(Integer code, String name) {
        this.setCode(code);
        this.setName(name);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
