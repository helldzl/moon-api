package com.mifan.article.web;

import com.mifan.article.domain.ForumCategories;
import com.mifan.article.service.ForumCategoriesService;

import io.jsonwebtoken.lang.Collections;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.Include;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/10/31
 */
@RestController
@RequestMapping("/forumCategories")
public class ForumCategoriesController extends RestfulController<ForumCategories> {

    @Autowired
    private ForumCategoriesService forumCategoriesService;
    
    /**
     * 美频分类查询
     * @param page
     * @param size
     * @param parentId
     * @param request
     * @return
     */
    @RequestMapping(value = "/mp/{id}", method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPageMp(@PathVariable Long id,
            @RequestParam(required = false) boolean child,
            @RequestParam(required = false) boolean childTopics,
            HttpServletRequest request){
        ForumCategories category = forumCategoriesService.findOneMp(id, child,childTopics);
        
        return ResponseEntity.ok(Responses.builder().data(category));
    }
            
    @Override
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPost(
            @RequestBody Data<ForumCategories> data) {

        return super.doPost(data);
    }

    @Override
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> doDelete(
            @PathVariable Long id) {

        return super.doDelete(id);
    }

    @Override
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPatch(
            @PathVariable Long id,
            @RequestBody Data<ForumCategories> data) {

        return super.doPatch(id, data);
    }

    @RequestMapping(value = "/otherChildren", method = RequestMethod.GET)
    public ResponseEntity<Response> otherCategories(Long parentId,Long excludeId){
    	Restrictions.put(Include.class, new Include(new String[] {ForumCategories.NODE_CHILDREN}));
    	ForumCategories result = Services.queryForObject(ForumCategories.class, parentId,null);
    	if(result != null && !Collections.isEmpty(result.getChildren())) {
    		List<ForumCategories> children = result.getChildren();
    		children = children.stream().filter(fc -> !(fc.getId().equals(excludeId))).collect(Collectors.toList());
    		result.setChildren(children);
    	}
    	
    	return ResponseEntity.ok(Responses.builder().data(result));
    	
    }
    
    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Response> doGet(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {

        return super.doGet(id, include);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {

        return super.doGetPage(page, size, sort, include);
    }

}
