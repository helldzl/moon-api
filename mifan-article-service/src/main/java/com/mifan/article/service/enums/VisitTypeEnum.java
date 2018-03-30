/**
 * 访问类型
 */
package com.mifan.article.service.enums;

/**
 * @author ZYW
 *
 */
public enum VisitTypeEnum {
    admin(0,"超级管理员"),
    auditor(1, "审核员"),
    mine(2,"普通用户");

    private Integer code;
    private String name;
    
    VisitTypeEnum(Integer code ,String name){
        this.code = code;
        this.name = name;
    }
    
    public static boolean exits(Integer code){
        for(VisitTypeEnum e : values()){
            if(e.getCode().equals(code)){
                return true;
            }
        }
        
        return false;
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
