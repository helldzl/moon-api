package com.mifan.user.web;

import com.mifan.user.domain.Roles;
import com.mifan.user.service.RolesService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/10
 */
@RestController()
@RequestMapping("/roles")
public class RolesController extends RestfulController<Roles> {

    @Autowired
    private RolesService rolesService;


    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Response> doPost(@RequestBody Data<Roles> data) {
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
            @RequestBody Data<Roles> data) {
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
            @RequestParam(required = false, name = "page[size]", defaultValue = "1000") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {
        return super.doGetPage(page, size, sort, include);
    }

    /**
     * <p>查找某个组下的所有角色列表</p>
     *
     * @param groupId groupId
     * @return ResponseEntity
     */
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/groups/{groupId}", method = RequestMethod.GET)
    public ResponseEntity<Response> doFindRoles(@PathVariable Long groupId) {
        return new ResponseEntity<>(Responses.builder().data(rolesService.findRoles(groupId)), HttpStatus.OK);
    }

    /**
     * <p>查找某一个站点下用户的角色集合, 不包括群组中的</p>
     *
     * @param siteId
     * @param userId
     * @return
     */
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/sites/{siteId}/users/{userId}", method = RequestMethod.GET)
    public ResponseEntity<Response> doFindRolesByUser(@PathVariable Long siteId, @PathVariable Long userId) {
        return ResponseEntity.ok().body(Responses.builder().data(rolesService.findRolesByUser(siteId, userId)));
    }

    /**
     * <p>保存某个角色下的权限集合</p>
     *
     * @param roleId       roleId
     * @param authorityIds authorityIds
     * @return ResponseEntity
     */
    @RequestMapping(value = "/{roleId}/authorities", method = RequestMethod.POST)
    public ResponseEntity<Response> doSaveRolesAuthorities(@PathVariable Long roleId, String[] authorityIds) {
        switch (rolesService.addAuthorities(roleId,convert(Arrays.asList(authorityIds)))) {
            case 0:
                throw new IllegalStateException();
            case ACCEPTED:
                return ResponseEntity.accepted().build();
            case NO_CONTENT:
                return ResponseEntity.noContent().build();
            default:
                Map<String, Object> result = new HashMap<>(2);
                result.put("type", "roles/authorities");
                result.put("id", roleId);
                return ResponseEntity.created(URI.create("/user/roles/" + roleId + "/authorities")).body(Responses.builder().data(result));
        }
    }

}
