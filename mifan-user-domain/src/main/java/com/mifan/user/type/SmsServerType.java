/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.user.type.SmsServerType
 *
 * @description:短信服务商类别信息
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
public enum SmsServerType {
    
    ali_server(0, "http://gw.api.taobao.com/router/rest","23709037","4ae71afdab2d88251f06f11221f0445f"),
    
    lean_server(1,"https://api.leancloud.cn/1.1/requestSmsCode","q3MBxj7nUEB6KrmhBXGBEy4s","AVVK3ngA0gci4p30C7JJIO2r-gzGzoHsz");
    
    
    private final int index;
    private final String server;
    private final String appKey;
    private final String appSecret;
    SmsServerType(int index, String server, String appKey,
                  String appSecret) {
        this.index = index;
        this.server = server;
        this.appKey = appKey;
        this.appSecret = appSecret;
    }
    public int getIndex() {
        return index;
    }
    public String getServer() {
        return server;
    }
    public String getAppKey() {
        return appKey;
    }
    public String getAppSecret() {
        return appSecret;
    }
    
}
