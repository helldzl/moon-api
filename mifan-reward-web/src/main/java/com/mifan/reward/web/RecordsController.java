package com.mifan.reward.web;

import com.mifan.reward.domain.Records;
import com.mifan.reward.service.enums.EntityState;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.validation.ValidationGroups;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.moonframework.web.utils.NetWorkUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Api(description = "前台接口-参与记录-records相关API", basePath = "/records")
@RestController
@RequestMapping("/records")
public class RecordsController extends RestfulController<Records> {

    @ApiOperation(value = "根据id获取指定公告内容")
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

    @ApiOperation(value = "分页获取参与记录", notes = "添加筛选条件和排序字段 filter[enabled]=1&filter[prizeId]=1&filter[userId]")
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
    public ResponseEntity<Response> doPost(Data<Records> data) {
        Records saveRecord = data.getData();
        Long userId = getCurrentUserId();

        if (userId != null) {
            saveRecord.setCreator(userId);
            saveRecord.setModifier(userId);
        }
        HttpServletRequest request = getHttpServletRequest();

        saveRecord.setIp(NetWorkUtils.getIpAddress(request));
        saveRecord.setUserId(userId);
        switch (Services.save(Records.class, saveRecord)) {
            case 0:
                throw new IllegalStateException();
            case -4060:
                return ResponseEntity.badRequest().body(Responses.builder().error(Responses.error(
                        String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()),
                        HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                        "下单次数为负")));
            case -4061:
                return ResponseEntity.badRequest().body(Responses.builder().error(Responses.error(
                        String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()),
                        HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                        "用户米粒余额不足")));
            case -4062:
                return ResponseEntity.badRequest().body(Responses.builder().error(Responses.error(
                        String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()),
                        HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                        "奖品剩余夺宝次数不足")));
            default:
                Map<String, Object> result = new HashMap<>(2);
                result.put("type", "record");
                result.put("id", saveRecord.getId());
                return ResponseEntity.created(URI.create("/records" + saveRecord.getId())).body(Responses.builder().data(result));
        }
    }

    @Override
    protected Criterion criterion(Criterion criterion) {
        return Restrictions.and(
                Restrictions.eq(BaseEntity.ENABLED, EntityState.ENABLED.getState()),
                criterion);
    }
}
