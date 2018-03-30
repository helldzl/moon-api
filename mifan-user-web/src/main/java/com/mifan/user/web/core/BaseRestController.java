package com.mifan.user.web.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.moonframework.core.util.BeanUtils;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.ResourceObject;
import org.moonframework.web.jsonapi.Response;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.moonframework.web.core.RestfulController.controller;
import static org.moonframework.web.core.RestfulController.get;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/15
 * @deprecated
 */
// @RestController
// @RequestMapping("/api")
@Deprecated
public class BaseRestController {

    private static Log logger = LogFactory.getLog(BaseRestController.class);

    /**
     * <p>新增一个资源</p>
     *
     * @param type Resource Object Type
     * @param data data
     * @return ResponseEntity
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{type}",
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPost(
            @PathVariable String type,
            @RequestBody Data<Map<String, Object>> data) {
        BaseEntity entity = BeanUtils.copyProperties(data.getData(), get(type));
        return controller(type).doPost(new Data<>(entity));
    }

    /**
     * <p>删除一个资源, 非物理删除(enabled = 0)</p>
     *
     * @param type Resource Object Type
     * @param id   ID
     * @return ResponseEntity
     */
    @RequestMapping(value = "/{type}/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> doDelete(
            @PathVariable String type,
            @PathVariable Long id) {
        return controller(type).doDelete(id);
    }

    /**
     * <p>修改资源</p>
     *
     * @param type Resource Object Type
     * @param id   ID
     * @param data data
     * @return ResponseEntity
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{type}/{id}",
            method = RequestMethod.PATCH,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPatch(
            @PathVariable String type,
            @PathVariable Long id,
            @RequestBody Data<Map<String, Object>> data) {
        BaseEntity entity = BeanUtils.copyProperties(data.getData(), get(type));
        return controller(type).doPatch(id, new Data<>(entity));
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{type}/{id}/relationships/{relationship}",
            method = RequestMethod.PATCH,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPatch(
            @PathVariable String type,
            @PathVariable Long id,
            @PathVariable String relationship,
            @RequestBody Data<Object> data) {
        Object obj = data.getData();
        if (obj instanceof List) {
            // to many patch
            List<ResourceObject> resources = ((List<Map<String, Object>>) obj).stream().map(map -> {
                ResourceObject resourceObject = new ResourceObject();
                BeanWrapper wrapper = new BeanWrapperImpl(resourceObject);
                wrapper.setPropertyValues(map);
                return resourceObject;
            }).collect(toList());
            return controller(type).doToManyPatch(id, relationship, new Data<>(resources));
        } else {
            // to one patch
            ResourceObject resource = BeanUtils.copyProperties(obj, ResourceObject.class);
            return controller(type).doToOnePatch(id, relationship, new Data<>(resource));
        }
    }

    @RequestMapping(value = "/{type}/{id}/relationships/{relationship}",
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doToManyPost(
            @PathVariable String type,
            @PathVariable Long id,
            @PathVariable String relationship,
            @RequestBody Data<List<ResourceObject>> data) {
        return controller(type).doToManyPost(id, relationship, data);
    }

    @RequestMapping(value = "/{type}/{id}/relationships/{relationship}",
            method = RequestMethod.DELETE,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doToManyDelete(
            @PathVariable String type,
            @PathVariable Long id,
            @PathVariable String relationship,
            @RequestBody Data<List<ResourceObject>> data) {
        return controller(type).doToManyDelete(id, relationship, data);
    }

    /**
     * <p>根据资源对象ID查找数据</p>
     *
     * @param type    Resource Object Type
     * @param id      ID
     * @param include include
     * @return ResponseEntity
     */
    @RequestMapping(value = "/{type}/{id}", method = RequestMethod.GET)
    public ResponseEntity<Response> doGet(
            @PathVariable String type,
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {
        return controller(type).doGet(id, include);
    }

    /**
     * <p>查询资源对象分页数据</p>
     *
     * @param type    Resource Object Type
     * @param page    current page, start from [1]
     * @param size    page size
     * @param sort    sort array
     * @param include include
     * @return ResponseEntity
     */
    @RequestMapping(value = "/{type}", method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPage(
            @PathVariable String type,
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {
        return controller(type).doGetPage(page, size, sort, include);
    }

}
