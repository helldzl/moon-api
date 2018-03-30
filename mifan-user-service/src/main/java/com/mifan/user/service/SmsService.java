/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.user.service.SmsService
 *
 * @description:短信验证码相关业务
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年4月13日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.user.service;

import java.util.Map;

import com.mifan.user.type.SpecType;

/**
 * @author ZYW
 *
 */
public interface SmsService {
    
    /**
     * 
     * @param phoneNum接收短信的手机号
     * @param serverType 选择服务商
     * @param sp  选择短信模版
     * @return
     */
    Map<String,String> sendVerifyCode(String phoneNum,Integer serverType,SpecType sp);
    
    /**
     * 校验短信码
     * @param phoneNum
     * @param smsCode
     * @param sp
     * @return
     */
    boolean verifySMSCode(String phoneNum,String smsCode, SpecType sp);
}
