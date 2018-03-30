package com.mifan.user.web;

import com.fasterxml.jackson.annotation.JsonView;
import com.mifan.user.domain.UserProfiles;
import com.mifan.user.domain.Users;
import com.mifan.user.service.UserProfilesService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.model.mybatis.criterion.QueryFieldOperator;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/27
 */
@RestController()
@RequestMapping("/profiles")
public class UserProfilesController extends RestfulController<UserProfiles> {

    @Autowired
    private UserProfilesService userProfilesService;

    /**
     * <p>编辑用户资料</p>
     *
     * @param id   id
     * @param data data
     * @return ResponseEntity
     */
    @RequiresAuthentication
    @RequestMapping(value = "/{id}",
            method = PATCH,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Response> doPatch(
            @PathVariable Long id,
            @RequestBody Data<UserProfiles> data) {

        hasPermissions(id);
        return super.doPatch(id, data);
    }

    @RequiresAuthentication
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Override
    public ResponseEntity<Response> doGet(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {

//        assertPermitted(id);
        hasPermissions(id);
        return super.doGet(id, include);
    }

    @JsonView(Users.WithoutPasswordView.class)
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
    @RequestMapping(value = "/{id}/admin", method = RequestMethod.GET)
    public ResponseEntity<Response> doGetAdmin(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {

        UserProfiles result = userProfilesService.findOneAdmin(id);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        Responses.DefaultBuilder builder = Responses.builder();
        Map<String, Object> meta = meta();
        if (!CollectionUtils.isEmpty(meta)) {
            builder.meta(meta);
        }
        return ResponseEntity.ok(builder.data(result));
    }

    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPageAdmin(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {

        HttpServletRequest request = getHttpServletRequest();
        Page<UserProfiles> result = userProfilesService.findAllAdmin(QueryFieldOperator.criterion(request.getParameterMap()), QueryFieldOperator.pageRequest(page, size, sort));
        return ResponseEntity.ok(Responses.builder().page(result, "/api/profiles", request.getParameterMap()).data(result.getContent()));
    }
}
