/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.support.web.CommentController
 *
 * @description:comment控制器
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年5月23日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.support.web;

import com.mifan.support.domain.Comment;
import com.mifan.support.domain.EventLog;
import com.mifan.support.service.CommentService;
import com.mifan.support.service.EventLogService;
import com.mifan.support.util.MyRunable;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.validation.ValidationGroups.Post;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.moonframework.web.utils.JsonMapper;
import org.moonframework.web.utils.NetWorkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author ZYW
 *
 */
@RestController
@RequestMapping("/comments")
public class CommentController extends RestfulController<Comment> {
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private EventLogService eventLogService;
    
    private final String userHeader = "X-User-ssid";
    
    private ExecutorService pool = Executors.newCachedThreadPool();
    
    @RequiresAuthentication
    @RequestMapping(method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Response> doPost(@RequestBody Data<Comment> data){
        logger.info(data.getData().toString());
        
        Comment entity = data.getData();
        Long userId = getCurrentUserId();
        if (userId != null) {
            entity.setCreator(userId);
            entity.setModifier(userId);
        }
        hasError(validate(entity, Post.class));
        afterPostValidate(entity);
        ResponseEntity<Response> response = null;
        switch (Services.save(entityClass, entity)) {
            case 0:
                throw new IllegalStateException();
            case ACCEPTED:
                response = ResponseEntity.accepted().build();
                break;
            case NO_CONTENT:
                response =  ResponseEntity.noContent().build();
                break;
            default:
                Map<String, Object> result = new HashMap<>(3);
                result.put("type", "comment");
                result.put("id", entity.getId());
                result.put("replayCount", entity.getReplayCount());
                response = ResponseEntity.created(URI.create("/support/comments" + "/" + entity.getId())).body(Responses.builder().data(result));
        }
        int isSuccess = response.getStatusCode().is2xxSuccessful() ? 1 : 0;
        HttpServletRequest request = getHttpServletRequest(); 
        log(request,data.getData(),userId,isSuccess);
        return response;
    }
    @RequiresAuthentication
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Override
    public ResponseEntity<Response> doDelete(@PathVariable Long id){//逻辑删除
        Comment comment = commentService.findOne(id);
        Long creator = comment == null ? null : comment.getCreator();
        assertPermitted(creator);
        HttpServletRequest request = getHttpServletRequest();
        
        ResponseEntity<Response> response = super.doDelete(id);
        
        int isSuccess = String.valueOf(response.getStatusCode()).startsWith("20") ? 1 : 0;
        log(request,null,creator,isSuccess);
        
        return response;
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Override
    public ResponseEntity<Response> doGet(@PathVariable Long id,
            @RequestParam(required = false) String[] include) {
        Long currentUserId = getCurrentUserId();
        HttpServletRequest request = getHttpServletRequest();
        log(request,null,currentUserId,1);
        return super.doGet(id, include);
    }
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Response> pageList(@RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = true, name = "filter[confId]")Long confId,
            @RequestParam(required = true, name = "filter[themeIds]")long[] themeIds,
            @RequestParam(required = true, name = "filter[topId]")Long topId){//前台展示
        
        Long currentUserId = getCurrentUserId();
        Page<Comment> result = commentService.doGetPage(currentUserId,confId, themeIds, topId, page, size);
        
        
        HttpServletRequest request = getHttpServletRequest();
//        log(request,null,currentUserId,1);
        return ResponseEntity.ok(Responses.builder().page(result, "/comments", request.getParameterMap()).data(result.getContent()));
    }
    @RequestMapping(value = "/theme",method = RequestMethod.GET)
    public ResponseEntity<Response> themeInfo(@RequestParam(required = true)Long confId,
            @RequestParam(required = true)Long themeId){
        Map<String,Object> map = commentService.themeInfoCount(confId, themeId);
        return new ResponseEntity<>(Responses.builder().data(map), HttpStatus.OK);
    }
    @RequestMapping(value = "/hots",method = RequestMethod.GET)
    public ResponseEntity<Response> hotComments(@RequestParam(required = true)Long confId,
            @RequestParam(required = true)long[] themeIds){
        Long currentUserId = getCurrentUserId();
        List<Comment> comments = commentService.findHotComments(currentUserId, confId, themeIds);
        return new ResponseEntity<>(Responses.builder().data(comments), HttpStatus.OK);
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
    private void log(HttpServletRequest request, Comment comment, Long userId, int isSuccess){
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
                if(comment != null){
                    String params = mapper.toJson(comment);
                    eventLog.setParams(params);
                }
                
                eventLog.setCreator(userId);
                eventLogService.save(eventLog);
            }
        });
    }
}
