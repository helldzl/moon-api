/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.user.type.WeChatType
 *
 * @description:微信公众号类型或者网页应用类型
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年4月19日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.user.type;

/**
 * @author ZYW
 *
 */
public enum WeChatType {
    
    wechat_public("wechatpub","wxd6142addd37f819a","17cf09357ce8f4fb121cf6df45fd0a13"),
    
    web_app("wechatweb","wx20250f71156b4d41","e6928acdc49dfc09d060898d006e4163");
    
    private final String state;
    private final String appid;
    private final String secret;
    WeChatType(String state, String appid, String secret) {
        this.state = state;
        this.appid = appid;
        this.secret = secret;
    }
    
    public static WeChatType getTypeByState(String state) {
        for (WeChatType t : WeChatType.values()) {
            if (t.getState().equals(state)) {
                return t;
            }
        }
        return null;
    }
    
    public String getState() {
        return state;
    }
    public String getAppid() {
        return appid;
    }
    public String getSecret() {
        return secret;
    }
    
}
