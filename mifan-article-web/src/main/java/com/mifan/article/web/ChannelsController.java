package com.mifan.article.web;


import com.mifan.article.domain.Channels;
import com.mifan.article.domain.Seeds;
import com.mifan.article.domain.UsersChannelsWatch;
import com.mifan.article.service.ChannelsService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.model.mybatis.criterion.*;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Include;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.validation.ValidationGroups.Post;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/6
 */
@RestController
@RequestMapping("/channels")
public class ChannelsController extends RestfulController<Channels> {

    @Autowired
    private ChannelsService channelsService;

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {
        // replace order by [cancellable asc, watched desc] to [reverse desc, watched desc], use index
        if (sort != null && sort.length == 2) {
            String s = sort[0];
            if (s.contains(Channels.CANCELLABLE)) {
                String order = "";
                if (!s.startsWith("-")) {
                    order = "-";
                }
                sort[0] = order + Channels.REVERSE;
            }
        }
        HttpServletRequest request;
        Page<Channels> result;
        try {
            request = getHttpServletRequest();
            if (size > 100) {
                size = 100;
            }
            if (size < 1) {
                size = 1;
            }
            if (include != null) {
                Restrictions.put(Include.class, new Include(include));
            }
            Criterion criterion = criterion(QueryFieldOperator.criterion(request.getParameterMap()));
            result = channelsService.findAll(criterion, QueryFieldOperator.pageRequest(page, size, sort), QueryFieldOperator.fields(request.getParameterMap()));
            page(result);
        } finally {
            Restrictions.remove();
        }
        return ResponseEntity.ok(Responses.builder().page(result, "/channels", request.getParameterMap()).data(result.getContent()));
    }

    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Response> doPost(@RequestBody Data<Channels> data) {
        Channels entity = data.getData();
        hasError(validate(entity, Post.class));
        Long currentUserId = getCurrentUserId();
        entity.setModifier(currentUserId);
        if (entity.getId() == null) {
            data.getData().setCreator(currentUserId);
        }
        channelsService.add(entity);
        Map<String, Object> result = new HashMap<>(16);
        result.put("type", "channels");
        result.put("id", entity.getId());
        return ResponseEntity.created(URI.create("channels" + "/" + entity.getId())).body(Responses.builder().data(result));
    }

    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}/enabled/{enabled}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> doEnabled(@PathVariable Long id, @PathVariable Integer enabled) {
        if (enabled != 1) {
            enabled = 0;
        }
        Channels c = new Channels(id);
        c.setEnabled(enabled);
        Services.update(Channels.class, c);
        return ResponseEntity.noContent().build();
    }

    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{channelId}/seeds", method = RequestMethod.GET)
    public ResponseEntity<Response> doGetSeeds(
            @PathVariable Long channelId,
            @RequestParam(required = false) String[] include) {
        List<Seeds> seeds = channelsService.findSeedsByChannel(channelId);
        return ResponseEntity.ok(Responses.builder().data(seeds));
    }

    /**
     * <p>用户频道</p>
     *
     * @param id      user id
     * @param include include
     * @return Response
     */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public ResponseEntity<Response> doGetOne(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {
        return super.doGet(channelsService.channel(id), include);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Override
    public ResponseEntity<Response> doGet(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {
        return super.doGet(id, include);
    }

    @Override
    protected Criterion criterion(Criterion criterion) {
        if (criterion instanceof Junction) {
            Junction junction = (Junction) criterion;
            boolean exists = false;
            for (Criterion c : junction.conditions()) {
                if (c instanceof SimpleExpression) {
                    SimpleExpression expression = (SimpleExpression) c;
                    if (expression.getPropertyName().equalsIgnoreCase(UsersChannelsWatch.TABLE_NAME + "." + UsersChannelsWatch.USER_ID)) {
                        assertPermitted(Long.valueOf((String) expression.getValue()));
                        exists = true;
                    }
                }
            }
            if (exists) {
                junction.add(Restrictions.eq(UsersChannelsWatch.TABLE_NAME + "." + BaseEntity.ENABLED, 1));
            }
        }
        return Restrictions.and(
                Restrictions.eq(Channels.TABLE_NAME + "." + BaseEntity.ENABLED, 1),
                criterion);
    }

    @Override
    protected void page(Page<Channels> page) {
        channel(page.getContent());
    }

    @Override
    protected void afterGet(Channels channel) {
        channel(Collections.singletonList(channel));
    }

    private void channel(List<Channels> list) {
        // TODO Use LEFT JOIN instead
        if (getSubject().isAuthenticated()) {
            Set<Long> set = Services.findAll(
                    UsersChannelsWatch.class,
                    Restrictions.and(
                            Restrictions.eq(UsersChannelsWatch.USER_ID, getCurrentUserId()),
                            Restrictions.eq(UsersChannelsWatch.ENABLED, 1)),
                    Fields.builder()
                            .add(UsersChannelsWatch.USER_ID)
                            .add(UsersChannelsWatch.CHANNEL_ID).build())
                    .stream()
                    .map(UsersChannelsWatch::getChannelId)
                    .collect(Collectors.toSet());
            list.forEach(channel -> channel.setSubscribed(set.contains(channel.getId())));
        }
    }

}
