package com.mifan.article.web.admin;

import com.mifan.article.domain.Topics;
import com.mifan.article.domain.support.TopicIndex;
import com.mifan.article.service.BrandsService;
import com.mifan.article.service.SearchService;
import io.jsonwebtoken.lang.Collections;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.elasticsearch.action.delete.DeleteResponse;
import org.moonframework.concurrent.util.LockUtils;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/8/25
 */
@RestController
@RequestMapping("/admin/Index")
public class AdminIndexController extends RestfulController<Topics> {

    @Autowired
    private SearchService searchService;

    @Autowired
    private BrandsService brandsService;

    /**
     * batch index lock
     */
    private Lock lock = new ReentrantLock();

    /**
     * <p>根据ID集合重建索引, 一般是修复数据用</p>
     *
     * @param ids topics IDs
     * @return Response
     */
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(method = GET)
    public HttpEntity<Response> doOne(
            @RequestParam(value = "id") String[] ids) {
        searchService.index(Topics.class, ids);
        return ResponseEntity.ok(Responses.builder().data("Index successful!"));
    }

    /**
     * <p>根据ID集合重建索引, 一般是修复数据用</p>
     * <p>start, the (inclusive) initial value</p>
     * <p>end, the exclusive upper bound</p>
     *
     * @param data data
     * @return Response
     */
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> doOne(
            @RequestBody Data<TopicIndex> data) {

        TopicIndex index = data.getData();

        if (index.getId() != null) {
            searchService.index(Topics.class, index.getId().toString());
        } else if (!Collections.isEmpty(index.getArray())) {
            searchService.index(Topics.class, index.getArray().toArray(new String[index.getArray().size()]));
        } else if (index.getStart() > 0 && index.getEnd() > 0 && index.getEnd() > index.getStart()) {
            boolean executed = LockUtils.tryLock(lock, 5, () -> {
                IntStream.range(index.getStart(), index.getEnd()).forEach(value -> searchService.index(Topics.class, String.valueOf(value)));
                return true;
            });
            if (executed) {
                return ResponseEntity.ok(Responses.builder().data("Index status [TERMINATED]"));
            } else {
                return ResponseEntity.ok(Responses.builder().data("index status [RUNNABLE]"));
            }
        } else {
            throw new IllegalArgumentException("wrong parameters");
        }
        return ResponseEntity.ok(Responses.builder().data("Index successful!"));
    }

    /**
     * <p>根据ID区间重建索引</p>
     *
     * @param start the (inclusive) initial value
     * @param end   the exclusive upper bound
     * @return Response
     */
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/batch", method = {GET, POST})
    public HttpEntity<Response> doRange(
            @RequestParam int start,
            @RequestParam int end) {

        boolean executed = LockUtils.tryLock(lock, 5, () -> {
            IntStream.range(start, end).forEach(value -> searchService.index(Topics.class, String.valueOf(value)));
            return true;
        });

        if (executed) {
            return ResponseEntity.ok(Responses.builder().data("Index status [TERMINATED]"));
        } else {
            return ResponseEntity.ok(Responses.builder().data("index status [RUNNABLE]"));
        }

    }

    /**
     * <p>删除索引, 一般用于删除错误数据</p>
     *
     * @param id topic ID
     * @return Response
     */
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{id}", method = DELETE)
    public HttpEntity<Response> doDelete(@PathVariable String id) {
        DeleteResponse delete = searchService.delete(Topics.class, id);
        return ResponseEntity.ok(Responses.builder().data(delete));
    }

    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/cache/brand/{name}", method = GET)
    public HttpEntity<Response> doCache(@PathVariable String name) {
        boolean reset = brandsService.reset(name);
        return ResponseEntity.ok(Responses.builder().data(String.format("cached successful : %s", reset)));
    }

}
