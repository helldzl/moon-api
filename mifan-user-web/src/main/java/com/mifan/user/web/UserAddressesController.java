package com.mifan.user.web;

import com.mifan.user.domain.UserAddresses;
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

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/31
 */
@RestController
@RequestMapping("/addresses")
public class UserAddressesController extends RestfulController<UserAddresses> {

    @RequiresAuthentication
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Response> doPost(
            @RequestBody Data<UserAddresses> data) {
        Long userId = getCurrentUserId();
        UserAddresses entity = data.getData();
        entity.setUserId(userId);
        return super.doPost(data);
    }

    @RequiresAuthentication
    @RequestMapping(value = "/admin",
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPostAdmin(
            @RequestBody Data<UserAddresses> data) {
        return super.doPost(data);
    }

    @RequiresAuthentication
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Override
    public ResponseEntity<Response> doDelete(@PathVariable Long id) {
        UserAddresses resource = findOne(id);
        hasPermissions(resource == null ? null : resource.getUserId());
        return super.doDelete(id);
    }

    @RequiresAuthentication
    @RequestMapping(value = "/{id}",
            method = RequestMethod.PATCH,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Response> doPatch(
            @PathVariable Long id,
            @RequestBody Data<UserAddresses> data) {
        UserAddresses resource = findOne(id);
        hasPermissions(resource == null ? null : resource.getUserId());
        return super.doPatch(id, data);
    }

    @RequiresAuthentication
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Override
    public ResponseEntity<Response> doGet(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {
        UserAddresses resource = findOne(id);
        hasPermissions(resource == null ? null : resource.getUserId());
        return super.doGet(id, include);
    }

    @RequiresAuthentication
    @RequestMapping(method = RequestMethod.GET)
    @Override
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {
        return super.doGetPage(page, size, sort, include);
    }

    /**
     * 对于分页查询, 只有管理员角色可以无限制查询, 普通用户只能查询自己的数据
     *
     * @param criterion criterion
     * @return Criterion
     */
    @Override
    protected Criterion criterion(Criterion criterion) {
        return Restrictions.and(
                Restrictions.eq(UserAddresses.USER_ID, getCurrentUserId()),
                Restrictions.eq(BaseEntity.ENABLED, 1),
                criterion);
    }

    private UserAddresses findOne(Long id) {
        return Services.findOne(UserAddresses.class, id, Fields.builder().add(UserAddresses.USER_ID).build());
    }

}
