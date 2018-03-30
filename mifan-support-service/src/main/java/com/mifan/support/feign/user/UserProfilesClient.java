package com.mifan.support.feign.user;

import org.moonframework.web.jsonapi.Data;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mifan.support.domain.support.UserProfiles;
import com.mifan.support.feign.FeignConfiguration;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;

/**
 * <p>返回类型可以是以下几种</p>
 * <ol>
 * <li>Data&lt;T&gt;</li>
 * <li>Data&lt;List&lt;T&gt;&gt;</li>
 * <li>ResponseEntity&lt;Data&lt;T&gt;</li>
 * <li>ResponseEntity&lt;Data&lt;List&lt;T&gt;&gt;</li>
 * </ol>
 *
 * @author quzile
 * @version 1.0
 * @since 2018/1/20
 */
@FeignClient(name = "micro-service-user", configuration = FeignConfiguration.class)
public interface UserProfilesClient {

    @RequestMapping(method = GET, value = "/profiles/{id}")
    ResponseEntity<Data<UserProfiles>> doGet(@PathVariable(value = "id") Long id, @RequestParam(required = false) Map<String, Object> params);

    @RequestMapping(method = GET, value = "/profiles/{id}")
    ResponseEntity<Data<UserProfiles>> doGet(@PathVariable(value = "id") Long id);

    @RequestMapping(method = GET, value = "/profiles")
    ResponseEntity<Data<List<UserProfiles>>> doGetPage(@RequestParam(value = "params", required = false) Map<String, Object> params);

    @RequestMapping(method = PATCH, value = "/profiles/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Data<Void>> doPatch(@PathVariable(value = "id") Long id, @RequestBody Data<UserProfiles> data);

}
