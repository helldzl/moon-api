/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.user.type.RegisterErr
 *
 * @description:TODO
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年4月6日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.user.type;

/**
 * @author ZYW
 *
 */
public enum RegisterErr {
    
    sms_code_err("100", "手机验证码不正确"),
    
    sms_serv_fail("101","阿里短信服务失败"),
    
    sms_count_excess("102","短信验证码获取次数过多！"),
    
    sms_description_err("103","短信类型错误或为空"),
    
    user_not_exists("201","用户不存在！"),
    
    user_exists("202","用户已存在"),

    verifycode_err("300","图形验证码不正确，或未能获取验证码！"),
    
    phoneNum_err("400","手机号码有误！"),
    
    password_atypism("401","两次输入的密码不一致"),
    
    password_err("402","密码验证失败"),
    
    ip_sms_count_excess("500","同一个ip获取验证码次数过多"),
    
    user_save_err("600","保存失败"),
    
    refresh_token_fail("700","oldToken不合法或者已过期"),
    
    refresh_token_many("701","刷新token过于频繁"),
    
    token_notfount("703","服务端不存在此token"),
    
    token_err("704","token不合法或者已经过期"),
    
    wechat_login_err("800","微信登录失败"),
    
    wechat_state_err("801","微信应用类型不正确"),
    
    wx_redpacket_err("802","系统放红包失败，将会在24小时内重发"),
    
    wx_redpacket_conformity("803","不符合领红包的条件"),
    
    wx_not_follow("804","用户未关注"),
    
    wechat_code_err("805","用户未授权");

    private final String index;
    private final String name;
    
    public static String getNameByIndex(String index) {
        for (RegisterErr e : RegisterErr.values()) {
            if (e.getIndex().equals(index)) {
                return e.getName();
            }
        }
        return null;
    }

    RegisterErr(String index, String name) {
        this.index = index;
        this.name = name;
    }

    public String getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

}
