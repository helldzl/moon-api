package com.mifan.reward.web;

import com.mifan.reward.domain.Goods;
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

@Api(description = "前台接口-商品信息-goods相关API", basePath = "/goods")
@RestController
@RequestMapping("/goods")
public class GoodsController extends RestfulController<Goods> {

    @ApiOperation(value = "根据id获取指定商品信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "id", dataType = "long", value = "记录id")
    })
    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Response> doGet(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {
        return super.doGet(id, include);
    }

    @ApiOperation(value = "分页获取商品信息", notes = "添加筛选条件和排序字段 filter[enabled]=1&filter[prizeId]=1&filter[userId]")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "page[number]", dataType = "int", value = "页码", defaultValue = "1" ),
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
