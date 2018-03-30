package com.mifan.article.web;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mifan.article.domain.UserSearchHistory;
import com.mifan.article.service.UserSearchHistoryService;

/**
 * @author ZYW
 *
 */
@RestController
@RequestMapping("/userSearchHistorys")
public class UserSearchHistoryController extends RestfulController<UserSearchHistory>{

    private final String userHeader = "X-User-ssid";
    
    @Autowired
    private UserSearchHistoryService userSearchHistoryService;
    
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Response> findHistoryBySsid(
            @RequestParam(required = false, name = "filter[categoryId]", defaultValue = "0") long categoryId,
            @RequestParam(required = false, name = "filter[forumId]", defaultValue = "0") long forumId) {
        HttpServletRequest request = getHttpServletRequest();
        String ssid = request.getHeader(userHeader);
        List<String> result = userSearchHistoryService.findHistoryBySsid(ssid, categoryId, forumId);
        return ResponseEntity.ok(Responses.builder().data(result));
    }
    
    @RequestMapping(method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Response> doPost(@RequestBody Data<UserSearchHistory> data){
        HttpServletRequest request = getHttpServletRequest();
        String ssid = request.getHeader(userHeader);
        Long userId = getCurrentUserId();
        data.getData().setSsid(ssid);
        data.getData().setUserId(userId);
        return super.doPost(data);
    }
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Response> doDelete(@RequestParam(required = true, name = "keyword") String keyword){
        HttpServletRequest request = getHttpServletRequest();
        String ssid = request.getHeader(userHeader);
        int n = userSearchHistoryService.deleteKeyword(ssid, keyword);
        switch (n) {
            case 0:
                return ResponseEntity.notFound().build();
            case ACCEPTED:
                return ResponseEntity.accepted().build();
            default:
                return ResponseEntity.noContent().build();
        }
    }
}
