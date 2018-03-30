/**
 * 去重信息状态
 */
package com.mifan.article.service.enums;

/**
 * @author ZYW
 *
 */
public enum ProductRepeatState {
    ready(0,"待审核"),
    pass(1, "通过"),
    no_pass(2,"未通过");

    private Integer code;
    private String name;
    
    ProductRepeatState(Integer code ,String name){
        this.code = code;
        this.name = name;
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
