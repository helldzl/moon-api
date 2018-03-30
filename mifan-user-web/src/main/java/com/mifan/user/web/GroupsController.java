package com.mifan.user.web;

import com.mifan.user.domain.Groups;
import com.mifan.user.domain.GroupsRoles;
import com.mifan.user.service.GroupsService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.validation.ValidationGroups.Post;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/10
 */
@RestController()
@RequestMapping("/groups")
public class GroupsController extends RestfulController<Groups> {

    @Autowired
    private GroupsService groupsService;

    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Response> doPost(@RequestBody Data<Groups> data) {
        return super.doPost(data);
    }
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}",
            method = RequestMethod.PATCH,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Response> doPatch(
            @PathVariable Long id,
            @RequestBody Data<Groups> data) {
        return super.doPatch(id, data);
    }
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Override
    public ResponseEntity<Response> doDelete(@PathVariable Long id) {
        return super.doDelete(id);
    }
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Override
    public ResponseEntity<Response> doGet(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {
        return super.doGet(id, include);
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

    /**
     * <p>查找某个站点下的某个用户所在的用户群组</p>
     *
     * @param siteId siteId
     * @param userId userId
     * @return ResponseEntity
     */
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/sites/{siteId}/users/{userId}", method = RequestMethod.GET)
    public ResponseEntity<Response> doFindGroups(@PathVariable Long siteId, @PathVariable Long userId) {
        List<Groups> result = groupsService.findGroups(siteId, userId);
        return ResponseEntity.ok().body(Responses.builder().data(result));
    }

    /**
     * <p>保存某个组下的角色</p>
     *
     * @param groupId groupId
     * @param roleIds roleIds
     * @return ResponseEntity
     */
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{groupId}/roles", method = RequestMethod.POST)
    public ResponseEntity<Response> doSaveGroupsRoles(@PathVariable Long groupId, @RequestBody Data<GroupsRoles> data) {
        hasError(validate(data.getData(), Post.class));
        if (0 == groupsService.addRoles(groupId, data.getData().getRoleIds())) {
            throw new RuntimeException();
        }
        return ResponseEntity.ok(null);
    }

}
