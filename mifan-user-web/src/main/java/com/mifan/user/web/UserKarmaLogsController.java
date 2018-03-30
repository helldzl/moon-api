package com.mifan.user.web;

import com.mifan.user.domain.UserKarmaLogs;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.validation.ValidationGroups;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.mifan.user.domain.UserKarmaLogs.USER_ID;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * <p>用户米粒/积分记录</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2017/3/31
 */
@RestController()
@RequestMapping("/grains")
public class UserKarmaLogsController extends RestfulController<UserKarmaLogs> {

    /**
     * <p>只有[每日签到]是主动事件, 提供一个API, 将该事件记录到积分日志表中, 并进行相关的事务操作</p>
     *
     * @param data data
     * @return ResponseEntity
     */
    @RequiresAuthentication
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Response> doPost(@RequestBody Data<UserKarmaLogs> data) {
        UserKarmaLogs entity = data.getData();
        assertPermitted(entity.getUserId());
        hasError(validate(entity, ValidationGroups.Post.class));
        afterPostValidate(entity);
        switch (Services.save(entityClass, entity)) {
            case 0:
                throw new IllegalStateException();
            case ACCEPTED:
                return ResponseEntity.accepted().build();
            case NO_CONTENT:
                return ResponseEntity.noContent().build();
            default:
                Map<String, Object> result = new HashMap<>(3);
                result.put("type", "grains");
                result.put("id", entity.getId());
                result.put("scores", entity.getScore());
                return ResponseEntity.created(URI.create("/api/grains/" + entity.getId())).body(Responses.builder().data(result));
        }
    }

    @RequiresAuthentication
    @RequestMapping(method = RequestMethod.GET)
    @Override
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {
        return super.doGetPage(page, size, sort, include);
    }

    @Override
    protected Criterion criterion(Criterion criterion) {
        if (isAdmin()) {
            return criterion;
        }
        return Restrictions.and(
                Restrictions.eq(USER_ID, getCurrentUserId()),
                criterion);
    }

    @Override
    protected void afterPostValidate(UserKarmaLogs entity) {
        entity.setAction(UserKarmaLogs.Event.SIGN);
    }
}
