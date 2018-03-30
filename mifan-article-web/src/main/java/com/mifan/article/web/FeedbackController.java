package com.mifan.article.web;

import com.mifan.article.domain.Feedback;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/9/2
 */
@RestController
@RequestMapping("/feedback")
public class FeedbackController extends RestfulController<Feedback> {

    @Override
//    @RequiresAuthentication
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)

    public ResponseEntity<Response> doPost(
            @RequestBody Data<Feedback> data) {

        return super.doPost(data);
    }

    @Override
    @RequiresAuthentication
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {

        return super.doGetPage(page, size, sort, include);
    }

    @Override
    @RequiresAuthentication
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Response> doGet(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {

        return super.doGet(id, include);
    }
}
