package com.mifan.support.web;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.moonframework.security.authentication.UserPermissions;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/11
 */
@RestController
@RequestMapping("/upload")
public class FileUploadController {

    // @Value("${application.file.upload.key1}")
    private String key1 = "o2o&: JDm@Q中国（*@DYdm!~@#_+?><!@+~_DASM@_aq*SBG";

    // @Value("${application.file.upload.key2}")
    private String key2 = "SA)FJAqwe892-^%@(QAWE*_@!H@E_!@)~(≧▽≦)/~啦啦啦";

    // @Value("${application.file.upload.code}")
    private String code = "20160725001";

    /**
     * @return 获取上传文件前的验证数据
     */
    @RequiresAuthentication
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Response> doGet() {
        long time = System.currentTimeMillis();
        long userId = ((UserPermissions) SecurityUtils.getSubject().getPrincipal()).getUserId();
        int isPublic = 1;
        String key = DigestUtils.md5Hex(code + time + key1 + isPublic + userId + code + time + key2 + isPublic + userId);

        Map<String, Object> map = new HashMap<>(5);
        map.put("isPublic", 1);
        map.put("code", code);
        map.put("key", key);
        map.put("time", time);
        map.put("userId", userId);
        return ResponseEntity.ok(Responses.builder().data(map));
    }

}
