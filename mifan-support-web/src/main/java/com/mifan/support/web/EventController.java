/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.support.web.EventController
 *
 * @description:用户行为日志相关
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年4月24日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.support.web;

import com.mifan.support.domain.EventLog;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.utils.NetWorkUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author ZYW
 *
 */
@RestController
@RequestMapping("/eventLogs")
public class EventController extends RestfulController<EventLog>{
    private static Log logger = LogFactory.getLog(EventController.class);
    
    private final String userHeader = "X-User-ssid";
    
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(method = RequestMethod.GET)
    @Override
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {
        return super.doGetPage(page, size, sort, include);
    }
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Override
    public ResponseEntity<Response> doGet(@PathVariable Long id,
            @RequestParam(required = false) String[] include) {
        return super.doGet(id, include);
    }
    @RequestMapping(method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> post(@RequestBody Data<EventLog> data, HttpServletRequest request){
        String ssid = request.getHeader(userHeader);
        
        String ua = request.getHeader("User-Agent");
        String ip = NetWorkUtils.getIpAddress(request);
        Long currentUserId = getCurrentUserId();
        EventLog eventLog = data.getData();
        eventLog.setUa(ua);
        eventLog.setIp(ip);
        eventLog.setIsWechat(isWeChat(request));
        eventLog.setCreator(currentUserId);
        eventLog.setSsid(ssid);
        logger.info(data.getData().toString());
        return super.doPost(data);
        
    }
    /**
     * 判断是否是微信访问
     * 
     * @param request
     * @return
     */
    private int isWeChat(HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent").toLowerCase();
        return userAgent == null || userAgent.indexOf("micromessenger") == -1 ? 0 : 1;
    }
    
}
