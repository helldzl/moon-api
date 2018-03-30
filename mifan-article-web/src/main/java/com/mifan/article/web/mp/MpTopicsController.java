/**
 * 美频topics
 * 2018年3月22日 14:46:26
 */
package com.mifan.article.web.mp;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.elasticsearch.SearchResult;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mifan.article.domain.Attachments;
import com.mifan.article.domain.ForumCategories;
import com.mifan.article.domain.MpCategories;
import com.mifan.article.domain.Posts;
import com.mifan.article.domain.PostsText;
import com.mifan.article.domain.Topics;
import com.mifan.article.domain.support.ValidationGroups.MpPost;
import com.mifan.article.service.TopicsFetchService;
import com.mifan.article.service.TopicsService;

/**
 * @author ZYW
 *
 */
@RestController
@RequestMapping("/mp/topics")
public class MpTopicsController extends RestfulController<Topics> {
    
    public static final String MP_ROLE_ADMIN = "MP_ROLE_ADMIN";
    
    @Autowired
    private TopicsService topicsService;
    @Autowired
    private TopicsFetchService topicsFetchService;
    
    /**
     * 
     * @param page
     * @param size
     * @param fromDays
     * @param sort
     * @param request
     * @return
     */
    @RequestMapping(value = "/supports", method = RequestMethod.GET)
    public HttpEntity<Response> commonProblems(@RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) Integer fromDays,
            HttpServletRequest request){
        List<Map<String, Object>> list = topicsService.hotSearch(page, size, fromDays,null, new String[] {"-items.reviews"}, request);
        size = size - list.size();
        if(size > 0) {
            Set<String> excludes = list.stream().map(data -> String.valueOf(data.get("id"))).collect(Collectors.toSet());
            List<Map<String, Object>> more = topicsService.hotSearch(page, size, null,excludes, new String[]{"-created"}, request);
            list.addAll(more);
        }
        
        return ResponseEntity.ok(Responses.builder().data(list));
    }    
    /**
     * 美频推荐娱乐新闻
     * @param seedIds
     * @param size
     * @param request
     * @return
     */
    @RequestMapping(value = "/anchorRecommends", method = RequestMethod.GET)
    public HttpEntity<Response> anchorRecommend(@RequestParam(required = true, name = "filter[seedIds]") String seedIds,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            HttpServletRequest request){
        SearchResult<Map<String, Object>> result = topicsFetchService.mpRecommendNews(seedIds, size);
        return ResponseEntity.ok(topicsService.convert(1, size, result, request.getParameterMap()));
    }
    /**
     * 美频精选
     * @param size
     * @param forumIds
     * @return
     */
    @RequestMapping(value = "/hotnews",method = RequestMethod.GET)
    public ResponseEntity<Response> hotNews(@RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) Integer fromDays,
            HttpServletRequest request){
        List<Map<String, Object>> list = topicsService.hotSearch(1, size, fromDays,null, new String[] {"-items.reviews"}, request);
        size = size - list.size();
        if(size > 0) {
            Set<String> excludes = list.stream().map(data -> String.valueOf(data.get("id"))).collect(Collectors.toSet());
            List<Map<String, Object>> more = topicsService.hotSearch(1, size, null,excludes, new String[]{"-created"}, request);
            list.addAll(more);
        }
        return ResponseEntity.ok(Responses.builder().data(list));
    }
    
    @Override
    @RequiresAuthentication
    @RequiresRoles(MP_ROLE_ADMIN)
    @RequestMapping(method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPost(
            @RequestBody Data<Topics> data) {
        Topics entity = data.getData();
        hasError(validate(entity, MpPost.class));
        afterPostValidate(entity);
        switch (Services.save(Topics.class, entity)) {
            case 0:
                throw new IllegalStateException();
            case ACCEPTED:
                return ResponseEntity.accepted().build();
            case NO_CONTENT:
                return ResponseEntity.noContent().build();
            default:
                Map<String, Object> result = new HashMap<>(16);
                result.put("type", "topics");
                result.put("id", entity.getId());
                return ResponseEntity.created(URI.create("/mp/topics/" + entity.getId())).body(Responses.builder().data(result));
        }
    }
    
    /**
     * 美频热榜新闻
     * @param seedIds
     * @param page
     * @param size
     * @param request
     * @return
     * 此接口2.3.0版本中删除
     */
    /*@RequestMapping(value = "/mpHot", method = RequestMethod.GET)
    public HttpEntity<Response> mpHotNews(@RequestParam(required = true, name = "filter[seedIds]") String seedIds,
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            HttpServletRequest request){
        SearchResult<Map<String, Object>> result = topicsFetchService.mpHotNews(seedIds, size);
        return ResponseEntity.ok(topicsService.convert(page, size, result, request.getParameterMap()));
    }*/
    
    @Override
    protected void afterPostValidate(Topics topic) {
        if(Topics.ForumType.MPSUPPORT.getIndex() == topic.getForumId()) {
            if(topic.getCategoryId() == null) {
                throw new IllegalStateException("请填写米饭分类信息");
            }else {
                ForumCategories find = new ForumCategories(topic.getCategoryId());
                find.setLeaf(1);
                find.setEnabled(1);
                if(!Services.exists(ForumCategories.class, find)) {
                    throw new IllegalStateException("米饭分类信息不合法！");
                }
            }
            MpCategories mc = new MpCategories(topic.getMp().getMpCategoryId());
            mc.setLeaf(1);
            mc.setType(2);
            mc.setEnabled(1);
            mc = Services.findOne(MpCategories.class, mc);
            if(topic.getMp().getMpCategoryId() == null) {
                throw new IllegalStateException("请填写美频分类信息");
            }else {
                if(mc == null) {
                    throw new IllegalStateException("美频分类信息不合法！");
                }
            }
            /*MpBrandCategories mbc = new MpBrandCategories();
            mbc.setMpCategoryId(mc.getParentId());
            mbc = Services.findOne(MpBrandCategories.class, mbc);
            if(mbc == null) {
                throw new IllegalStateException("美频分类信息不合法！");
            }
            MpBrands mb = Services.findOne(MpBrands.class, mbc.getBrandId());
            TopicsDocument document = topic.getDocument();
            if(document == null) {
                document = new TopicsDocument();
            }
            document.setBrand(mb.getTitle());*/
        }
        
        Long userId = getCurrentUserId();
        Posts post = topic.getPost();
        post.setParentId(0L);

        topic.setTitle(post.getTitle());

        PostsText text = new PostsText();
        text.setTitle(post.getTitle());
        text.setDescription(post.getDescription());
        text.setContent(post.getContent());
        if(!CollectionUtils.isEmpty(post.getCategories())){
            StringBuffer categories = new StringBuffer("");
            post.getCategories().forEach(c -> categories.append(c).append(","));
            categories.deleteCharAt(categories.length() -1 );
            text.setCategory(categories.toString());
        }
        if(!CollectionUtils.isEmpty(post.getTags())){
            StringBuffer tags = new StringBuffer("");
            post.getTags().forEach(t -> tags.append(t).append(","));
            tags.deleteCharAt(tags.length() - 1);
            text.setTag(tags.toString());
        }
        post.setPostsText(text);

        List<Attachments> attachments = topic.getAttachments();
        if (!CollectionUtils.isEmpty(attachments)) {
            attachments.stream().filter(a -> a.getId() != null).collect(Collectors.toList());
        }

        if(topic.getId() != null) {
            if (post.getCreator() == null) {//因为posts中creator是唯一索引的因素，所以更新时必须要存在
                throw new IllegalStateException("缺失创建人信息");
            }
        }else {
            setCreate(topic, userId);
            setCreate(post, userId);
        }
        setModify(topic, userId);
        setModify(post, userId);
    }
    private <S extends BaseEntity> S setModify(S entity, Long currentUserId){
        Date now = new Date();
        entity.setModified(now);
        entity.setModifier(currentUserId);
        return entity;
    }
    private <S extends BaseEntity> S setCreate(S entity,Long currentUserId){
        Date now = new Date();
        entity.setCreated(now);
        if(entity.getCreator() == null) {
            entity.setCreator(currentUserId);
        }
        return entity;
    }
}
