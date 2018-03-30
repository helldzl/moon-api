/**
 * 微信相关接口
 */
package com.mifan.user.service;

import com.mifan.user.domain.support.Signature;

/**
 * @author ZYW
 *
 */
public interface WeChatService {
    /**
     * 获取业务token（微信）
     * @return
     */
    String getBusinessAccessToken();
    
    /**
     * 生成签名
     * @param url
     * @return
     */
    Signature signature(String url);
}
