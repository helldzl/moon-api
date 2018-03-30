package com.mifan.user.web;

import com.mifan.user.domain.Authorities;
import com.mifan.user.service.AuthoritiesService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
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
@RestController
@RequestMapping("/authorities")
public class AuthoritiesController extends RestfulController<Authorities> {

    @Autowired
    private AuthoritiesService authoritiesService;

    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Response> doPost(@RequestBody Data<Authorities> data) {
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
            @RequestBody Data<Authorities> data) {
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
     * <p>查找某个角色下的所有权限列表</p>
     *
     * @param roleId roleId
     * @return ResponseEntity
     */
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/roles/{roleId}", method = RequestMethod.GET)
    public ResponseEntity<Response> doFindAuthorities(@PathVariable Long roleId) {
        List<Authorities> results = authoritiesService.findAuthorities(roleId);
        return ResponseEntity.ok().body(Responses.builder().data(results));
    }
}
