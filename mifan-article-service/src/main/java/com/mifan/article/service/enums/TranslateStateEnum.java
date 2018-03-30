package com.mifan.article.service.enums;


/**
 * Created by zhanghuan on 2017/1/18.
 */
public enum TranslateStateEnum {
    DRAFT(1, "草稿箱"),
    SUBMIT(2, "待审核"),
    SUCCESS(3, "审核通过"),
    FAILURE(4, "审核失败"),
    COVERED(5,"被覆盖"),
    DELETE(9, "已删除");

    private Integer code;
    private String name;

    TranslateStateEnum(Integer code, String name) {
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
