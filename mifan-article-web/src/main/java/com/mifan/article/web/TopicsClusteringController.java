/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.article.web.TopicsClusteringController
 *
 * @description:TODO
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年8月15日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.article.web;

import com.mifan.article.domain.TopicsClustering;
import com.mifan.article.service.TopicsClusteringService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author ZYW
 *
 */
@RestController
@RequestMapping("/topicsClusterings")
public class TopicsClusteringController extends RestfulController<TopicsClustering>{
    
    @Autowired
    private TopicsClusteringService topicsClusteringService;

    @Override
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPost(
            @RequestBody Data<TopicsClustering> data) {

        Long currentUserId = getCurrentUserId();
        int result = topicsClusteringService.repeat(data.getData(), currentUserId);
        return new ResponseEntity<>(Responses.builder().data(result), HttpStatus.OK);
    }
    
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> delete(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        int result = topicsClusteringService.delete(id, currentUserId);
        return new ResponseEntity<>(Responses.builder().data(result), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Response> pageList(@RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = true, name = "filter[topicId]")Long topicId,
            @RequestParam(required = false, name = "filter[targetId]")Long targetId){
        
        Page<TopicsClustering> result = topicsClusteringService.findPage(topicId, targetId, page, size);
        HttpServletRequest request = getHttpServletRequest();
        
        return ResponseEntity.ok(Responses.builder().page(result, "/topicsClusterings", request.getParameterMap()).data(result.getContent()));
    }
}
