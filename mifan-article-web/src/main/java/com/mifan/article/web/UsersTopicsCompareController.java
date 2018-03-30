package com.mifan.article.web;

import com.mifan.article.domain.UsersTopicsCompare;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/usersFoldersCompare")
public class UsersTopicsCompareController extends RestfulController<UsersTopicsCompare> {

    @Override
    @RequiresAuthentication
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPost(
            @RequestBody Data<UsersTopicsCompare> data) {
        throw new UnsupportedOperationException("请参考 [POST] /users/{userId}/relationships/folders");
//        UsersTopicsCompare compare = data.getData();
//        Long userId = compare.getUserId();
//        if (userId == null) {
//            compare.setUserId(getCurrentUserId());
//        } else {
//            hasPermissions(userId);
//        }
//        return super.doPost(data);
    }

    @Override
    @RequiresAuthentication
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> doDelete(
            @PathVariable Long id) {

        permitted(id);
        return super.doDelete(id);
    }

    @Override
    @RequiresAuthentication
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPatch(
            @PathVariable Long id,
            @RequestBody Data<UsersTopicsCompare> data) {

        permitted(id);
        return super.doPatch(id, data);
    }

    @Override
    @RequiresAuthentication
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Response> doGet(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {

        permitted(id);
        return super.doGet(id, include);
    }

    @Override
    @RequiresAuthentication
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {

        return super.doGetPage(page, size, sort, include);
    }

    @Override
    protected Criterion criterion(Criterion criterion) {
        return Restrictions.and(
                Restrictions.eq(UsersTopicsCompare.USER_ID, getCurrentUserId()),
                Restrictions.eq(BaseEntity.ENABLED, 1),
                criterion);
    }

    private void permitted(Long id) {
        if (!isRoleAdmin()) {
            UsersTopicsCompare resource = Services.findOne(UsersTopicsCompare.class, id, Fields.builder().add(UsersTopicsCompare.USER_ID).build());
            hasPermissions(resource == null ? null : resource.getUserId());
        }
    }

}
