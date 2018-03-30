/**
 * 精翻
 */
package com.mifan.article.web;

import com.mifan.article.domain.Posts;
import com.mifan.article.domain.PostsText;
import com.mifan.article.domain.support.ValidationGroups.TranslationPatch;
import com.mifan.article.domain.support.ValidationGroups.TranslationPost;
import com.mifan.article.service.PostsService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author ZYW
 *
 */
@RestController
@RequestMapping("/humanTranslates")
public class HumanTranslateController extends RestfulController<Posts> {

    @Autowired
    private PostsService postsService;

    @RequiresAuthentication
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false, name = "filter[parentTitle]") String parentTitle,
            @RequestParam(required = false, name = "filter[topicId]") Long topicId,
            @RequestParam(required = false, name = "filter[title]") String title,
            @RequestParam(required = false, name = "filter[state]") Integer state) {

        HttpServletRequest request = getHttpServletRequest();

        Posts post = new Posts();
        post.setTitle(title);
        post.setParentTitle(parentTitle);
        post.setTopicId(topicId);
        post.setState(state);
        if(!isRoleAdmin() && !isRoleAuditor()) {
            post.setCreator(getCurrentUserId());
            post.setIsAdmin(false);
        }

        Page<Posts> result = postsService.findPageHumanTranslate(post, page, size);

        return ResponseEntity.ok(Responses.builder().page(result, "/article/humanTranslates", request.getParameterMap()).data(result.getContent()));
    }

    @Override
    @RequiresAuthentication
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
     public ResponseEntity<Response> doGet(
                @PathVariable Long id,
                @RequestParam(required = false) String[] include) {

        Posts posts = postsService.findOne(id);
        Long userId = posts == null ? null : posts.getCreator();
        //只能本人和管理员以及审核员才能使用这个接口查看详情
        if(!isRoleAuditor()) {
            hasPermissions(userId);
        }

        return super.doGet(id, include);

    }

    @Override
    @RequiresAuthentication
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> doDelete(
            @PathVariable Long id){

        //此处的删除是将其审核相关数据的state改成9
        Posts posts = postsService.findOne(id);
        Long userId = posts == null ? null : posts.getCreator();
        hasPermissions(userId);

        if(postsService.deleteHumanTranslate(id) == 0) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @Override
    @RequiresAuthentication
    @RequiresRoles(ROLE_AD_USER)
    @RequestMapping(method = RequestMethod.POST,
              consumes = APPLICATION_JSON_VALUE,
              produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPost(
            @RequestBody Data<Posts> data) {

        hasError(validate(data.getData(), TranslationPost.class));
        this.setPostsText(data.getData());
        return super.doPost(data);
    }

    @Override
    @RequiresAuthentication
    @RequestMapping(value = "/{id}",method = RequestMethod.PATCH,
              consumes = APPLICATION_JSON_VALUE,
              produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPatch(
            @PathVariable Long id,
            @RequestBody Data<Posts> data) {

        Posts posts = postsService.findOne(id);
        Long userId = posts == null ? null : posts.getCreator();
        hasPermissions(userId);
        hasError(validate(data.getData(), TranslationPatch.class));
        this.setPostsText(data.getData());

        return super.doPatch(id, data);
    }

    private void setPostsText(Posts posts) {
        PostsText text = new PostsText();
        text.setTitle(posts.getTitle());
        text.setDescription(posts.getDescription());
        text.setContent(posts.getContent());
        posts.setPostsText(text);
    }

}
