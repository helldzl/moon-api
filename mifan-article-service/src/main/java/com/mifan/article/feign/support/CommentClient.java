package com.mifan.article.feign.support;

import com.mifan.article.domain.support.Comment;
import com.mifan.article.feign.FeignConfiguration;
import org.moonframework.web.jsonapi.Data;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author ZYW
 * 2018-01-31
 */
@FeignClient(name = "micro-service-support", configuration = FeignConfiguration.class)
public interface CommentClient {

    @RequestMapping(method = GET, value = "/comments")
    ResponseEntity<Data<List<Comment>>> doGetPage(@RequestParam(value = "params", required = true) Map<String, Object> params);

    @RequestMapping(method = POST, value = "/comments", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Data<Map<String, Object>>> doPost(@RequestBody Data<Comment> data);

    @RequestMapping(method = DELETE, value = "/comments/{id}")
    ResponseEntity<Data<Void>> doDelete(@PathVariable(value = "id") Long id);

    @RequestMapping(method = GET, value = "/comments/{id}")
    ResponseEntity<Data<Comment>> doGet(@PathVariable(value = "id") Long id);

    @RequestMapping(method = GET, value = "/comments/theme")
    ResponseEntity<Data<Map<String, Object>>> topicInfo(@RequestParam(value = "params", required = true) Map<String, Object> params);

    @RequestMapping(method = GET, value = "/comments/hots")
    ResponseEntity<Data<List<Comment>>> hotComments(@RequestParam(value = "params", required = true) Map<String, Object> params);
}
