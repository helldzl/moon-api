/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.user.web.WechatController
 *
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年4月20日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.user.web;

import com.mifan.user.domain.support.Signature;
import com.mifan.user.service.AuthService;
import com.mifan.user.service.WeChatService;
import com.mifan.user.type.RegisterErr;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author ZYW
 *
 */
@Controller
public class WechatController {
    
    private static final String ERR = "err";
    
    private static final String ACCESS_TOKEN = "access_token";
    
    private static final String OPEN_ID = "openid";
    
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    @Value("${moon.data.token.redirect}")
    private String tokenRedirect;
    
    @Value("${moon.data.wxpay.redirect}")
    private String redRedirect;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private WeChatService weChatService;
    
    @RequestMapping(value = "/oauth/weChat", method = RequestMethod.GET)
    public String weChat(String code,String state){
        Map<String,String> map = authService.weChat(code, state);
        if(map.containsKey(ERR)){
            return "redirect:" + tokenRedirect + "?" + ERR + "=" + map.get(ERR);
        }else{//access_token
            return "redirect:" + tokenRedirect + "?" + ACCESS_TOKEN + "=" + map.get(ACCESS_TOKEN);
        }
    }
    
    @RequestMapping(value = "/zhangyongwei", method = RequestMethod.GET)
    public String redPacket(String code,String state){
        Map<String,String> map = authService.redPacket(code, state);
        StringBuffer url = new StringBuffer("redirect:").append(redRedirect);
        if(map.containsKey(OPEN_ID)) {
            url.append("?").append(OPEN_ID).append("=").append(map.get(OPEN_ID));
            if(map.containsKey(ERR)){
                url.append("&").append(ERR).append("=").append(map.get(ERR));
            }
        }else {
            if(map.containsKey(ERR)){
                url.append("?").append(ERR).append("=").append(map.get(ERR));
            }
        }
        
        return url.toString();
        
    }
    @RequestMapping(value = "/isFollow", method = RequestMethod.GET)
    @ResponseBody
    public Map<String,String> isFollow(@RequestParam(required = true) String openid){
        Map<String,String> map = authService.isFollow(openid);
        return map;
        /*if(map.containsKey(ERR) && map.get(ERR).equals(RegisterErr.wx_not_follow.getIndex())) {
            return "redirect:http://192.168.1.187:3000/judge?"+ ERR + "=" + map.get(ERR);
        }
        return "redirect:http://192.168.1.187:3000";*/
        
    }
    @RequestMapping(value = "/oauth/weChat/bind", method = RequestMethod.GET)
    public String weChatBind(String code,String state){//手机绑定微信：当前登录帐号是手机
        String phoneToken = null;
        String stateNew = null;
        try{
            String[] arr = state.split(",");
            phoneToken = arr[0];
            stateNew = arr[1];
        }catch(Exception e){
            logger.error(RegisterErr.wechat_state_err.getName());
            return "redirect:http://www.mifanxing.com/?"+ERR + "=" + RegisterErr.wechat_state_err.getIndex();
        }
        Map<String,String> map = authService.weChat(code, stateNew);
        if(!map.containsKey(ERR)){
            map = authService.phoneBindWeChat(phoneToken, map.get(ACCESS_TOKEN));
        }
        if(!map.containsKey(ERR)){
            return "redirect:http://www.mifanxing.com/?bind" + "=on";
        }
        logger.error(RegisterErr.getNameByIndex(map.get(ERR)));
        return "redirect:http://www.mifanxing.com/?"+ERR + "=" + map.get(ERR);
    }
    @RequestMapping(value = "/wechat/signature", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Response> signature(@RequestParam(value = "url") String url) {

        Signature signature =  weChatService.signature(url);
        return ResponseEntity.ok(Responses.builder().data(signature));
    }
}
