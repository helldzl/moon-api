/**
 * 
 */
package com.mifan.article.web.admin;

import com.mifan.article.domain.Posts;
import com.mifan.article.service.PostsService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/admin/Posts")
public class AdminPostsController extends RestfulController<Posts> {

    @Autowired
    private PostsService postsService;

    // TODO 优化此处的接口结构
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/enabled/{id}",method = RequestMethod.GET)
    public ResponseEntity<Response> enabled(
            @PathVariable Long id,
            @RequestParam Integer enabled) {

        Posts posts = new Posts(id);
        if(enabled != 1) {
            posts.setEnabled(0);
        } else {
            posts.setEnabled(1);
        }
        postsService.update(posts);
        return ResponseEntity.ok(null);
    }
    @Override
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPost(
            @RequestBody Data<Posts> data){

        data.getData().setterText();
        return super.doPost(data);
    }

    @Override
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}",method = RequestMethod.PATCH,
              consumes = APPLICATION_JSON_VALUE,
              produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPatch(
            @PathVariable Long id,
            @RequestBody Data<Posts> data) {

        data.getData().setterText();
        return super.doPatch(id, data);
    }

    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value="/topic/{topicId}",method = RequestMethod.GET)
    public ResponseEntity<Response> doGetAllPosts(
            @PathVariable Long topicId,
            @RequestParam(required = false,name = "filter[enabled]") Integer enabled){

        List<Posts> posts = postsService.getAllPosts(topicId, enabled);
        return ResponseEntity.ok(Responses.builder().data(posts));
    }


    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value="/{id}",method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPosts(
            @PathVariable Long id) {

        Posts posts = postsService.findOne(id);
        return ResponseEntity.ok(Responses.builder().data(posts));
    }
}
