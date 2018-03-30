/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.user.web.RegisterController
 *
 * @description:注册功能
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
package com.mifan.user.web;

import com.alibaba.druid.util.StringUtils;
import com.mifan.user.domain.Users;
import com.mifan.user.service.SmsService;
import com.mifan.user.service.UsersService;
import com.mifan.user.type.RegisterErr;
import com.mifan.user.type.SpecType;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Error;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.moonframework.web.utils.NetWorkUtils;
import org.moonframework.web.utils.ValidatorCodeUtils;
import org.moonframework.web.utils.ValidatorCodeUtils.ValidatorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ZYW
 *
 */
@RestController
@RequestMapping("/register")
public class SmsController extends RestfulController<Users>{
    
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private static final String ERR = "err";
    
//    private static final String ERR_MSG = "err_message";
    
    private static final String IP_KEY = "ip_key";
    
    private static final String REGISTER_VERIFY_CODE = "REGISTER_VERIFY_CODE";
    
    @Autowired
    private UsersService usersService;
    
    @Autowired
    private SmsService smsService;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @RequestMapping(value = "/send_sms_code",method = RequestMethod.GET)
    public ResponseEntity<Response> sendSmsCode(HttpServletRequest request,
            @RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "verifyCode", required = true) String verifyCode,
            @RequestParam(value = "description", required = true) String description,
            Integer serverType) {
        
        String code = this.redisTemplate.opsForValue().get(getCodeKey(username));
        if(code == null || !code.equals(verifyCode)){
            Error error = Error.builder()
                    .code(RegisterErr.verifycode_err.getIndex())
                    .detail(RegisterErr.verifycode_err.getName()).build();
            return new ResponseEntity<>(Responses.builder().error(error), HttpStatus.BAD_REQUEST);
        }else{
            this.redisTemplate.delete(getCodeKey(username));//应及时删除redis中保存的图形验证码
        }
        
        String ip = NetWorkUtils.getIpAddress(request);
        if(!StringUtils.isEmpty(ip)){//同一个ip5个小时内最多获取100个短信验证码，通过redis中的计数判断，防止恶意刷短信
            String count = this.redisTemplate.opsForValue().get(getIpKey(ip));
            long ipSmsCount = 0;
            if(count != null){
                ipSmsCount = Long.parseLong(count);
            }
            if(ipSmsCount > 100){
                Error error = Error.builder()
                        .code(RegisterErr.ip_sms_count_excess.getIndex())
                        .detail(RegisterErr.ip_sms_count_excess.getName()).build();
                return new ResponseEntity<>(Responses.builder().error(error), HttpStatus.FORBIDDEN);
            }
            ipSmsCount ++ ;
            this.redisTemplate.opsForValue().set(getIpKey(ip), ipSmsCount+"", 18000, TimeUnit.SECONDS);//将这个ip获取验证码的计数记录在redis中，如果这个ip注册成功了，则清空这个计数
        }
        
        // 确认短信验证码是否发送成功
        Map<String,String> map = smsService.sendVerifyCode(username,serverType, SpecType.getValueByDesc(description));
        if (map.containsKey(ERR)){
            Error error = Error.builder()
                    .code(map.get(ERR))
                    .detail(RegisterErr.getNameByIndex(map.get(ERR))).build();
            return new ResponseEntity<>(Responses.builder().error(error), HttpStatus.BAD_REQUEST);
        }else {
            return ResponseEntity.noContent().build();
        }
    }
    private String getIpKey(String ip){
        return IP_KEY + ip;
    }

    @RequestMapping(value="/check.jpg", method = RequestMethod.GET)
    public void createCode(@RequestParam(value = "username", required = true) String username,
            HttpServletRequest request, HttpServletResponse response) throws IOException {  
        // 禁止图像缓存
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg"); 
        ValidatorCode codeUtil = ValidatorCodeUtils.getCode();
        // 存到缓存
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        // 将验证码存在缓存中
        ops.set(getCodeKey(username), codeUtil.getCode(), 600, TimeUnit.SECONDS);
        // 输出打web页面  
        ImageIO.write(codeUtil.getImage(), "jpg", response.getOutputStream());
    }
    
    private String getCodeKey(String username){
        return REGISTER_VERIFY_CODE + username;
    }
}
