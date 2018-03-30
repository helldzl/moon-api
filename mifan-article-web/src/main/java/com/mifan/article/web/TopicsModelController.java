package com.mifan.article.web;

import com.mifan.article.domain.TopicsModel;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mifan.article.domain.TopicsModel.ModelStatus;
import static com.mifan.article.domain.TopicsModel.Type;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/8
 */
@RestController
@RequestMapping("/topicsModel")
public class TopicsModelController extends RestfulController<TopicsModel> {

    @Override
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPost(
            @RequestBody Data<TopicsModel> data) {

        return super.doPost(data);
    }

    @Override
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> doDelete(
            @PathVariable Long id) {

        return super.doDelete(id);
    }

    @Override
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPatch(
            @PathVariable Long id,
            @RequestBody Data<TopicsModel> data) {

        String modelStatus = data.getData().getModelStatus();
        if (modelStatus != null && ModelStatus.valueOf(modelStatus).getType() != Type.MANUAL) {
            throw new IllegalStateException(String.format("仅支持 %s 状态", ModelStatus.from(Type.MANUAL)));
        }

        return super.doPatch(id, data);
    }

    @Override
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Response> doGet(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {

        return super.doGet(id, include);
    }

    @Override
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {

        return super.doGetPage(page, size, sort, include);
    }

    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/modelStatus", method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "filter[type]") Type type) {

        Set<Type> set = type == null ? Collections.emptySet() : Stream.of(type).collect(Collectors.toSet());
        return ResponseEntity.ok(Responses.builder().data(ModelStatus.from(set)));
    }

}
