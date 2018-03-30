package com.mifan.article.web;

import com.mifan.article.domain.VotesOption;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/votes/option")
public class VotesOptionController extends RestfulController<VotesOption> {

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
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
     public ResponseEntity<Response> doGet(
                @PathVariable Long id,
                @RequestParam(required = false) String[] include) {

        return super.doGet(id, include);
    }


}
