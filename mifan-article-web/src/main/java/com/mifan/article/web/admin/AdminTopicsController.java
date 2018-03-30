package com.mifan.article.web.admin;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.QueryFieldOperator;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Include;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mifan.article.domain.Attachments;
import com.mifan.article.domain.Posts;
import com.mifan.article.domain.PostsText;
import com.mifan.article.domain.Topics;
import com.mifan.article.domain.TopicsDocument;
import com.mifan.article.domain.support.ValidationGroups;

@RestController
@RequestMapping("/admin/Topics")
public class AdminTopicsController extends RestfulController<Topics> {

    /**
     * 根据forum_id不同保存不同类型的文章数据
     * @param data
     * @return
     */
    @Override
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPost(
            @RequestBody Data<Topics> data) {
        Topics entity = data.getData();
        postValidate(entity);
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
                return ResponseEntity.created(URI.create("/article/admin/Topics/" + entity.getId())).body(Responses.builder().data(result));
        }
    }

    /**
     * 根据id删除指定的文章（逻辑删除）
     * @param id
     * @return
     */
    @Override
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> doDelete(
            @PathVariable Long id){
        return super.doDelete(id);
    }

    /**
     * <p>修改主题, 主要用于加入/撤销训练样本集, 修改助推数操作, 以及启用/禁用</p>
     *
     * @param id   id
     * @param data data
     * @return Response
     */
    @Override
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPatch(
            @PathVariable Long id,
            @RequestBody Data<Topics> data) {
        return super.doPatch(id, data);
    }

    /**
     * 根据id查看指定的文章详情，与前台调用的接口返回数据一致
     * @param id
     * @param include
     * @return
     */
    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Response> doGet(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {

        Topics topics = Services.queryForObject(Topics.class, id,null);
        if (topics == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Responses.builder().data(topics));
    }

    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(method = RequestMethod.GET)
    @Override
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include ) {
        HttpServletRequest request;
        Page<Topics> result;
        try {
            request = getHttpServletRequest();
            if (size > 200) {
                size = 200;
            }
            if (size < 1) {
                size = 1;
            }
            if (include != null) {
                Restrictions.put(Include.class, new Include(include));
            }

            Criterion criterion = QueryFieldOperator.criterion(request.getParameterMap());
            result = Services.findAll(Topics.class, criterion, QueryFieldOperator.pageRequest(page, size, sort), true);
            page(result);
        } finally {
            Restrictions.remove();
        }
        return ResponseEntity.ok(Responses.builder().page(result, "/article/admin/Topics/", request.getParameterMap()).data(result.getContent()));
    }

    private void postValidate(Topics topic) {
        switch (topic.getForumId().intValue()) {
            case 1:
                break;
            case 2:
                hasError(validate(topic, ValidationGroups.BrandPost.class));
                break;
            case 3:
                hasError(validate(topic, ValidationGroups.NewsPost.class));
                break;
            case 4:
                hasError(validate(topic, ValidationGroups.NewsPost.class));
                break;
            case 5:
                hasError(validate(topic, ValidationGroups.VideosPost.class));
                break;
            case 6:
                hasError(validate(topic, ValidationGroups.NewsPost.class));
                break;
            case 7:
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    protected void afterPostValidate(Topics topic) {
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

        TopicsDocument document = topic.getDocument();
        if(document != null && document.getBrands() != null) {
            String brands = String.join(",", document.getBrands());
            document.setBrand(brands);
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
