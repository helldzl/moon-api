package com.mifan.article.web;

import com.mifan.article.domain.Links;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/12
 */
@RestController
@RequestMapping("/links")
public class LinksController extends RestfulController<Links> {

    @Override
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public ResponseEntity<Response> doGet(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {

        return super.doGet(id, include);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {

        return super.doGetPage(page, size, sort, include);
    }
}
