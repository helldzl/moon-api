package com.mifan.user.utils;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class CaptchaUtil {
    
    private static final String PASSWORD_ERR_COUNT_KEY = "password_err_count";
    
    private static final String LOGIN_VERIFY_CODE = "login_verify_code";
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    public Long addErrPasswordCount(String username){
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        String errCountStr = ops.get(getErrCountKey(username));
        Long errCount;
        if(errCountStr == null){
            errCount = 0L;
        }else{
            errCount = Long.parseLong(errCountStr);
        }
        errCount ++;
        ops.set(getErrCountKey(username), errCount+"", 300, TimeUnit.SECONDS);
        return errCount;
    }
    public Long getErrPasswordCount(String username){
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String errCountStr = ops.get(getErrCountKey(username));
        Long errCount;
        if(errCountStr == null){
            errCount = 0L;
        }else{
            errCount = Long.parseLong(errCountStr);
        }
        return errCount;
    }
    public void clearErrPasswordCount(String username){
        redisTemplate.delete(getErrCountKey(username));
    }
    public void addValidatorCode(String username,String code){
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(getCodeKey(username), code, 600, TimeUnit.SECONDS);
    }
    public String getValidatorCode(String username){
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.get(getCodeKey(username));
    }
    private String getErrCountKey(String username) {
        return PASSWORD_ERR_COUNT_KEY + username;
    }
    private String getCodeKey(String username){
        return LOGIN_VERIFY_CODE + username;
    }
}
