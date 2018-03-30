package com.mifan.article.web;

import com.mifan.article.domain.support.Comment;
import com.mifan.article.feign.support.CommentClient;
import com.mifan.article.service.CommentService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.moonframework.web.feign.Feigns;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author ZYW
 * 2018/1/31
 */
@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentClient commentClient;

    @RequiresAuthentication
    @RequestMapping(method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<? extends Response> doPost(@RequestBody Data<Comment> data) {
        return commentService.save(data);
    }

    @RequiresAuthentication
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<? extends Response> doDelete(@PathVariable Long id) {//逻辑删除
        return commentService.remove(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<? extends Response> doGet(@PathVariable Long id) {
        return commentClient.doGet(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<? extends Response> pageList(@RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
                                                       @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
                                                       @RequestParam(required = true, name = "filter[themeId]") Long themeId,
                                                       @RequestParam(required = true, name = "filter[topId]") Long topId) {//前台展示
        return commentService.doGetPage(themeId, topId, page, size);
    }

    @RequestMapping(value = "/theme", method = RequestMethod.GET)
    public ResponseEntity<? extends Response> themeInfo(@RequestParam(required = true) Long themeId) {
        return commentClient.topicInfo(Feigns.params().custom("themeId", themeId).custom("confId", 1).build());
    }

    @RequestMapping(value = "/hots", method = RequestMethod.GET)
    public ResponseEntity<? extends Response> hotComments(@RequestParam(required = true) Long topicId) {
        return commentService.findHotComments(topicId);
    }
}
