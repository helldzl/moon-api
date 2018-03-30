/**
 * 排行榜
 */
package com.mifan.support.web;

import com.mifan.support.domain.EventLog;
import com.mifan.support.domain.support.Rank;
import com.mifan.support.domain.support.Topics;
import com.mifan.support.service.RankService;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ZYW
 */
@RestController
@RequestMapping("/ranks")
public class RankController extends RestfulController<EventLog> {

    @Autowired
    private RankService rankService;

    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    public ResponseEntity<Response> doRank(@PathVariable String key) {
        rankService.updateRanking(key, 1);
        return ResponseEntity.ok(null);
    }

    @RequiresAuthentication
    @RequiresRoles(value = {ROLE_ADMIN, ROLE_REMOTE}, logical = Logical.OR)
    @RequestMapping(value = "/scores", method = RequestMethod.GET)
    public ResponseEntity<Response> doFindPageWithScore(@RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
                                                        @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
                                                        @RequestParam(required = true, name = "filter[key]") String key) {
        HttpServletRequest request = getHttpServletRequest();
        Page<Rank> result = rankService.findPageWithScore(key, page, size);
        return ResponseEntity.ok(Responses.builder().page(result, "/support/ranks/scores", request.getParameterMap()).data(result.getContent()));
    }

    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Response> remove(@RequestParam(required = false, name = "key") String key,
                                           @RequestParam(required = true, name = "topicId") Long topicId) {
        rankService.remove(key, topicId);
        return ResponseEntity.ok(null);
    }

//    @RequiresAuthentication
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Response> doFindPage(@RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
                                               @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
                                               @RequestParam(required = true, name = "filter[key]") String key) {
        HttpServletRequest request = getHttpServletRequest();
        Page<Topics> result = rankService.findPage(key, page, size);
        return ResponseEntity.ok(Responses.builder().page(result, "/support/ranks", request.getParameterMap()).data(result.getContent()));
    }
}
