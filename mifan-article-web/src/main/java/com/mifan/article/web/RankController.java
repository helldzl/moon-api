/**
 * 排行榜
 */
package com.mifan.article.web;

import com.mifan.article.service.RankService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.web.jsonapi.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZYW
 */
@RestController
@RequestMapping("/ranks")
public class RankController {

    @Autowired
    private RankService rankService;

    @RequiresAuthentication
    @RequiresRoles("ROLE_ADMIN")
    @RequestMapping(value = "/scores", method = RequestMethod.GET)
    public ResponseEntity<? extends Response> doFindPageWithScore(@RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
                                                                  @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
                                                                  @RequestParam(required = true, name = "filter[key]") String key) {
        return rankService.findPageWithScore(key, page, size);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<? extends Response> doFindPage(@RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
                                                         @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
                                                         @RequestParam(required = true, name = "filter[key]") String key) {
        return rankService.findPage(key, page, size);
    }
}
