/**
 * 
 */
package com.mifan.support.web;

import com.mifan.support.domain.EventDic;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.validation.ValidationGroups.Patch;
import org.moonframework.validation.ValidationGroups.Post;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author ZYW
 *
 */
@RestController
@RequestMapping("/eventDics")
public class EventDicController extends RestfulController<EventDic>{
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Response> doPost(@RequestBody Data<EventDic> data){
        logger.info(data.getData().toString());
        hasError(validate(data.getData(), Post.class));//校验必填参数
        return super.doPost(data);
    }
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}",method = RequestMethod.PATCH,
                    consumes = APPLICATION_JSON_VALUE,
                    produces = APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Response> doPatch(@PathVariable Long id, @RequestBody Data<EventDic> data) {
        hasError(validate(data.getData(), Patch.class));//校验必填参数
        Long currentUserId = getCurrentUserId();
        data.getData().setModifier(currentUserId);
        return super.doPatch(id, data);
    }
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Override
    public ResponseEntity<Response> doDelete(@PathVariable Long id){//逻辑删除
        return super.doDelete(id);
    }
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(method = RequestMethod.GET)
    @Override
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {
        return super.doGetPage(page, size, sort, include);
    }
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Override
    public ResponseEntity<Response> doGet(@PathVariable Long id,
            @RequestParam(required = false) String[] include) {
        return super.doGet(id, include);
    }
}
