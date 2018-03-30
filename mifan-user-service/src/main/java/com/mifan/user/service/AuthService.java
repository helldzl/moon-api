/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.user.service.AuthService
 *
 * @description:登录业务
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
package com.mifan.user.service;

import java.util.Map;

/**
 * @author ZYW
 *
 */
public interface AuthService {
    
    String BUSINESS_ACCESS_TOKEN_KEY = "business_token_key";
    
    String ACCESS_TOKEN = "access_token";
    
    /**
     * 登录认证成功后的操作，生成token
     * @param username
     * @param userId
     * @param rememberme
     * @return
     */
    Map<String, String> accessToken(String username,Long userId,int rememberme);
    
    /**
     * 根据在有效期内的token获取新的token
     * @param oldToken  现在的token
     * @return
     */
    Map<String,String> refreshToken(String oldToken);
    
    /**
     * 微信登录业务
     * @param code  用户授权code
     * @param state 类别
     * @return
     */
    Map<String,String> weChat(String code,String state);
    
    /**
     * 发红包接口
     * @param code
     * @param phone
     * @return
     */
    Map<String,String> redPacket(String code,String phone);
    /**
     * 判断用户是否已经关注
     * @param code
     * @param state
     * @return
     */
    Map<String,String> isFollow(String code,String state);
    /**
     * 根据openid判断用户是否已经关注
     * @param openid
     * @return
     */
    Map<String, String> isFollow(String openid);
    
    /**
     * 注销登录
     * @param token
     * @return
     */
    void logout(String token);
    /**
     * 当前登录帐号为微信帐号，绑定手机帐号
     * @param token  微信帐号登录获得的token
     * @param phone  要绑定的手机号
     * @return
     */
    void weChatBindPhone(String token,String phone);
    /**
     * 当前登录帐号为手机帐号，绑定微信帐号
     * @param phoneToken  手机帐号登录获得的token
     * @param wxToken  //要绑定的微信帐号认证获得的token
     */
    Map<String,String> phoneBindWeChat(String phoneToken,String wxToken);
}
