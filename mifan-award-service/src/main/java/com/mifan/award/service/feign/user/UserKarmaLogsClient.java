package com.mifan.award.service.feign.user;

import com.mifan.award.domain.support.UserKarmaLogs;
import com.mifan.award.service.feign.FeignConfiguration;
import org.moonframework.web.jsonapi.Data;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author quzile
 * @version 1.0
 * @since 2018/1/20
 */
@FeignClient(name = "micro-service-user", configuration = FeignConfiguration.class)
public interface UserKarmaLogsClient {

    @RequestMapping(method = POST, value = "/grains", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Data<Map<String, Object>>> doPost(@RequestBody Data<UserKarmaLogs> data);

}
