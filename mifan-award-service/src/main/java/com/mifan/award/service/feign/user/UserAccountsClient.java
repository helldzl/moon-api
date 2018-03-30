package com.mifan.award.service.feign.user;

import com.mifan.award.domain.support.UserAccounts;
import com.mifan.award.service.feign.FeignConfiguration;
import org.moonframework.web.jsonapi.Data;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author quzile
 * @version 1.0
 * @since 2018/1/23
 */
@FeignClient(name = "micro-service-user", configuration = FeignConfiguration.class)
public interface UserAccountsClient {

    @RequestMapping(method = GET, value = "/userAccounts")
    ResponseEntity<Data<List<UserAccounts>>> doGetPage(@RequestParam(value = "params", required = false) Map<String, Object> params);

}
