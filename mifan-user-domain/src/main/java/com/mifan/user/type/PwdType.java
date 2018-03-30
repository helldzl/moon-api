/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.user.type.PwdType
 *
 * @description:密码类型
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年4月17日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.user.type;

/**
 * @author ZYW
 *
 */
public enum PwdType {
    
    has_pwd(0,"正常的密码类型"),
    
    no_pwd(1,"无密码类型");//用户直接用手机或微信登录，后台自动注册时，应该设置该类型
    
    private final int index;
    private final String name;
    
    PwdType(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }
}
