package com.mifan.reward.web.admin;

import com.mifan.reward.domain.Categories;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(description = "后台管理-类别信息-categories相关API", basePath = "/admin/categories")
@RestController
@RequestMapping("/admin/categories")
public class AdminCategoriesController extends RestfulController<Categories> {
    @Override
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Response> doPost(
            @RequestBody Data<Categories> data) {
        data.getData().setCreator(getCurrentUserId());
        data.getData().setModifier(getCurrentUserId());
        return super.doPost(data);
    }

    @Override
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> doDelete(
            @PathVariable Long id) {
        return super.doDelete(id);
    }

    @Override
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}",method = RequestMethod.PATCH)
    public ResponseEntity<Response> doPatch(
            @PathVariable Long id,
            @RequestBody Data<Categories> data) {
        data.getData().setCreator(getCurrentUserId());
        data.getData().setModifier(getCurrentUserId());
        return super.doPatch(id, data);
    }

    @Override
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Response> doGet(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {
        return super.doGet(id, include);
    }

    @ApiOperation("分页获取类别信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "page[number]", dataType = "int", value = "页码", defaultValue = "1" )
    })
    @Override
    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {
        return super.doGetPage(page, size, sort, include);
    }

}
