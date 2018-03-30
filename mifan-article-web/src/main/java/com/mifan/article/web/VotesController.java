package com.mifan.article.web;

import com.mifan.article.domain.Votes;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/9/5
 */
@RestController
@RequestMapping("/votes")
public class VotesController extends RestfulController<Votes> {

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Response> doGet(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {

        return super.doGet(id, include);
    }

    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public ResponseEntity<Response> doGet(
            @RequestParam(required = false) String[] include) {

        return this.doGet(Votes.SYSTEM_VOTE_REPORT, include);
    }
    
    @RequestMapping(value = "/translate", method = RequestMethod.GET)
    public ResponseEntity<Response> doGetTranslate(
            @RequestParam(required = false) String[] include) {

        return this.doGet(Votes.TRANSLATE_REPORT, include);
    }
}
