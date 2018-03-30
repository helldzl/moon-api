/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.user.type.AliTemplae
 *
 * @description:阿里短信模版定义
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年4月10日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.user.type;

/**
 * @author ZYW
 *
 */
public enum AliTemplae {
    
    sms_register("SMS_60880396"),//用户注册模版
    
    sms_back_password("SMS_62005023"),//用户找回密码模版

    sms_login("SMS_61940052");//手机登录模版
    
    private String description;

    AliTemplae(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    
}
