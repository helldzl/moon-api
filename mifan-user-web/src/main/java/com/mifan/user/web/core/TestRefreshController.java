package com.mifan.user.web.core;

import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author quzile
 * @version 1.0
 * @since 2018/1/31
 */
@RefreshScope
@RestController("/test/refresh")
public class TestRefreshController {

    @Value("${myValue:default}")
    private String myValue;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Response> doGet() {
        return ResponseEntity.ok(Responses.builder().data(myValue));
    }

}
