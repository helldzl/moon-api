package com.mifan.reward.service.feign.user;

import com.mifan.reward.domain.support.Users;
import com.mifan.reward.service.feign.FeignConfiguration;
import org.moonframework.web.jsonapi.Data;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author quzile
 * @version 1.0
 * @since 2018/1/20
 */
@FeignClient(name = "micro-service-user", configuration = FeignConfiguration.class)
public interface UsersClient {

    @RequestMapping(method = GET, value = "/users/{id}")
    ResponseEntity<Data<Users>> doGet(@PathVariable(value = "id") Long id, @RequestParam(required = false) Map<String, Object> params);

    @RequestMapping(method = GET, value = "/users/{id}")
    ResponseEntity<Data<Users>> doGet(@PathVariable(value = "id") Long id);

    @RequestMapping(method = GET, value = "/users")
    ResponseEntity<Data<List<Users>>> doGetPage(@RequestParam(value = "params", required = false) Map<String, Object> params);

}
