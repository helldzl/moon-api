/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.support.web.PraiseController
 *
 * @description:
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年5月27日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.support.web;

import com.mifan.support.domain.EventLog;
import com.mifan.support.domain.Praise;
import com.mifan.support.service.EventLogService;
import com.mifan.support.util.MyRunable;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.moonframework.validation.ValidationGroups.Post;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.utils.JsonMapper;
import org.moonframework.web.utils.NetWorkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author ZYW
 *
 */
@RestController
@RequestMapping("/praises")
public class PraiseController extends RestfulController<Praise> {
    
    @Autowired
    private EventLogService eventLogService;
    
    private final String userHeader = "X-User-ssid";
    
    private ExecutorService pool = Executors.newCachedThreadPool();
    
    @RequiresAuthentication
    @RequestMapping(method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Response> doPost(@RequestBody Data<Praise> data){
        logger.info(data.getData().toString());
        hasError(validate(data.getData(), Post.class));//校验必填参数
        Long currentUserId = getCurrentUserId();
        Date now = new Date();
        data.getData().setCreated(now);
        data.getData().setModified(now);
        ResponseEntity<Response> response = super.doPost(data);
        
        int isSuccess = response.getStatusCode().is2xxSuccessful() ? 1 : 0;
        HttpServletRequest request = getHttpServletRequest(); 
        log(request,data.getData(),currentUserId,isSuccess);
        
        return response;
    }
    /**
     * 判断是否是微信访问
     * 
     * @param request
     * @return
     */
    private int isWeChat(HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent").toLowerCase();
        return !userAgent.contains("micromessenger") ? 0 : 1;
    }
    /**
     * 日志记录
     * @param request
     * @param comment
     * @param userId
     */
    private void log(HttpServletRequest request, Praise praise, Long userId, int isSuccess){
        int isWeChat = isWeChat(request);
        String ua = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");
        String url = request.getRequestURI();
        String methodType = request.getMethod();
        String ip = NetWorkUtils.getIpAddress(request);
        Map<String ,String[]> map = request.getParameterMap();//常用来做框架
        String ssid = request.getHeader(userHeader);
        pool.execute(new MyRunable(map) {
            @Override
            public void run() {
                EventLog eventLog = new EventLog();
                eventLog.setSource(referer);
                eventLog.setEventCode("comment_event");
                eventLog.setUrlLog(url);
                eventLog.setMethodType(methodType);
                eventLog.setIsWechat(isWeChat);
                eventLog.setUa(ua);
                eventLog.setIp(ip);
                eventLog.setIsSuccess(isSuccess);
                eventLog.setSsid(ssid);
                eventLog.setCreated(new Date());
                JsonMapper mapper = new JsonMapper();
                if(!params.isEmpty()){
                    eventLog.setParams(mapper.toJson(params));
                }
                if(praise != null){
                    String params = mapper.toJson(praise);
                    eventLog.setParams(params);
                }
                
                eventLog.setCreator(userId);
                eventLogService.save(eventLog);
            }
        });
    }
}
