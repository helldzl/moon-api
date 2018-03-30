package com.mifan.reward.web;


import com.mifan.reward.domain.Categories;
import com.mifan.reward.service.enums.EntityState;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(description = "前台接口-类别信息-categories相关API", basePath = "/categories")
@RestController
@RequestMapping("/categories")
public class CategoriesController extends RestfulController<Categories> {

    @ApiOperation(value = "分页获取类别信息", notes = "添加筛选条件和排序字段 filter[enabled]=1&sort=display_order")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "page[number]", dataType = "int", value = "页码", defaultValue = "1" ) ,
            @ApiImplicitParam(paramType = "query", name = "page[size]", dataType = "int", value = "页数", defaultValue = "10" )
    })
    @Override
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
                Restrictions.eq(BaseEntity.ENABLED, EntityState.ENABLED.getState()),
                criterion);
    }
}
