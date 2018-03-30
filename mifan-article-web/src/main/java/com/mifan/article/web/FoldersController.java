package com.mifan.article.web;

import com.mifan.article.domain.Folders;
import com.mifan.article.domain.UsersTopicsCompare;
import com.mifan.article.service.TopicsService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Include;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/folders")
public class FoldersController extends RestfulController<Folders> {

    @Autowired
    private TopicsService topicsService;

    @Override
    @RequiresAuthentication
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPost(
            @RequestBody Data<Folders> data) {

        Folders.FolderType type = Folders.FolderType.from(data.getData().getFolderType());
        if (type == null) {
            throw new IllegalArgumentException("Unknown folder type");
        }
        return super.doPost(data);
    }

    @Override
    @RequiresAuthentication
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> doDelete(
            @PathVariable Long id) {

        permitted(id);
        return super.doDelete(id);
    }

    @Override
    @RequiresAuthentication
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doPatch(
            @PathVariable Long id,
            @RequestBody Data<Folders> data) {

        permitted(id);
        return super.doPatch(id, data);
    }

    @Override
    @RequiresAuthentication
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Response> doGet(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {

        permitted(id);
        return super.doGet(id, include);
    }

    @Override
    @RequiresAuthentication
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {

        Long userId = getCurrentUserId();
        Folders.FolderType type = Folders.FolderType.from(getInteger("filter[folderType]"));
        if (type != null && page == 1 && !Services.exists(Folders.class, Restrictions.and(
                Restrictions.eq(Folders.CREATOR, userId),
                Restrictions.eq(Folders.ENABLED, 1),
                Restrictions.eq(Folders.FOLDER_TYPE, Folders.FolderType.COMPARE.getIndex())))) {
            // 初始化比较目录
            Folders folder = new Folders();
            folder.setFolderName("默认对比列表");
            folder.setFolderType(type.getIndex());
            folder.setCreator(userId);
            folder.setModifier(userId);
            folder.setCancellable(0);
            Services.save(Folders.class, folder);
        }

        return super.doGetPage(page, size, sort, include);
    }

    @Override
    protected void page(Page<Folders> page) {
        if (!Include.exists() || !page.hasContent()) {
            return;
        }

        // Restrictions.get(Include.class).stream().flatMap(s -> Arrays.stream(s.split(","))).map(Long::valueOf).collect(Collectors.toSet());
        // Restrictions.get(Include.class).stream().flatMap(s -> Arrays.stream(s.split(","))).map(Long::valueOf).distinct().toArray()
        Set<Long> set = Services.findAll(UsersTopicsCompare.class, Restrictions.and(
                Restrictions.eq(BaseEntity.ENABLED, 1),
                Restrictions.eq(UsersTopicsCompare.USER_ID, getCurrentUserId()),
                Restrictions.in(UsersTopicsCompare.TOPIC_ID, Restrictions.get(Include.class).stream().flatMap(s -> Arrays.stream(s.split(","))).map(Long::valueOf).distinct().toArray()),
                Restrictions.in(UsersTopicsCompare.FOLDER_ID, page.getContent().stream().map(Folders::getId).distinct().toArray())),
                Fields.builder()
                        .add(UsersTopicsCompare.FOLDER_ID)
                        .build())
                .stream()
                .map(UsersTopicsCompare::getFolderId)
                .collect(toSet());

        for (Folders folder : page) {
            folder.setChecked(set.contains(folder.getId()));
        }
    }

    @RequiresAuthentication
    @RequestMapping(value = "/{id}/compare", method = {RequestMethod.GET, RequestMethod.POST})
    public HttpEntity<Response> compare(@PathVariable Long id) {

        Set<String> set = Services.findAll(UsersTopicsCompare.class, Restrictions.and(
                Restrictions.eq(UsersTopicsCompare.USER_ID, getCurrentUserId()),
                Restrictions.eq(UsersTopicsCompare.FOLDER_ID, id),
                Restrictions.eq(BaseEntity.ENABLED, 1)),
                Fields.builder()
                        .add(UsersTopicsCompare.TOPIC_ID)
                        .build())
                .stream()
                .map(UsersTopicsCompare::getTopicId)
                .map(String::valueOf)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return ResponseEntity.ok(topicsService.compare(set));
    }

    @Override
    protected Criterion criterion(Criterion criterion) {
        return Restrictions.and(
                Restrictions.eq(BaseEntity.CREATOR, getCurrentUserId()),
                Restrictions.eq(BaseEntity.ENABLED, 1),
                criterion);
    }

    private void permitted(Long id) {
        if (!isRoleAdmin()) {
            Folders resource = Services.findOne(Folders.class, id, Fields.builder().add(Folders.CREATOR).build());
            hasPermissions(resource == null ? null : resource.getCreator());
        }
    }

}
