package com.mifan.article.web;

import com.mifan.article.domain.support.Users;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.ResourceObject;
import org.moonframework.web.jsonapi.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * <p>用户与主题关联关系控制器</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2017/7/5
 */
@RestController
public class UsersTopicsController extends RestfulController<Users> {

    @Override
    @RequiresAuthentication
    @RequestMapping(value = "/users/{id}/relationships/{relationship:topics}",
            method = RequestMethod.PATCH,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doToManyPatch(
            @PathVariable Long id,
            @PathVariable String relationship,
            @RequestBody Data<List<ResourceObject>> data) {

        assertPermitted(id);
        return super.doToManyPatch(id, relationship, data, false);
    }

    @RequiresAuthentication
    @RequestMapping(value = "/users/{id}/relationships/{relationship:topics}/{sub:hide|like|report|favorite}",
            method = RequestMethod.PATCH,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doToManyPatch(
            @PathVariable Long id,
            @PathVariable String relationship,
            @PathVariable String sub,
            @RequestBody Data<List<ResourceObject>> data) {

        assertPermitted(id);
        meta(data, sub);
        return super.doToManyPatch(id, relationship, data, false);
    }

    @Override
    @RequiresAuthentication
    @RequestMapping(value = "/users/{id}/relationships/{relationship:topics}",
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doToManyPost(
            @PathVariable Long id,
            @PathVariable String relationship,
            @RequestBody Data<List<ResourceObject>> data) {

        assertPermitted(id);
        return super.doToManyPost(id, relationship, data, false);
    }

    @RequiresAuthentication
    @RequestMapping(value = "/users/{id}/relationships/{relationship:topics}/{sub:hide|like|report|favorite}",
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doToManyPost(
            @PathVariable Long id,
            @PathVariable String relationship,
            @PathVariable String sub,
            @RequestBody Data<List<ResourceObject>> data) {

        assertPermitted(id);
        meta(data, sub);
        return super.doToManyPost(id, relationship, data, false);
    }

    @Override
    @RequiresAuthentication
    @RequestMapping(value = "/users/{id}/relationships/{relationship:topics}",
            method = RequestMethod.DELETE,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doToManyDelete(
            @PathVariable Long id,
            @PathVariable String relationship,
            @RequestBody Data<List<ResourceObject>> data) {

        assertPermitted(id);
        return super.doToManyDelete(id, relationship, data, false);
    }

    @RequiresAuthentication
    @RequestMapping(value = "/users/{id}/relationships/{relationship:topics}/{sub:hide|like|report|favorite}",
            method = RequestMethod.DELETE,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doToManyDelete(
            @PathVariable Long id,
            @PathVariable String relationship,
            @PathVariable String sub,
            @RequestBody Data<List<ResourceObject>> data) {

        assertPermitted(id);
        meta(data, sub);
        return super.doToManyDelete(id, relationship, data, false);
    }

    private void meta(Data<List<ResourceObject>> data, String sub) {
        Map<String, Object> meta = data.getMeta();
        if (meta == null) {
            meta = new HashMap<>(16);
            data.setMeta(meta);
        }
        meta.put("relationships", sub);
    }

}
