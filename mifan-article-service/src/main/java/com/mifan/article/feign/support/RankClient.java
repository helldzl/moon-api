package com.mifan.article.feign.support;

import com.mifan.article.domain.Topics;
import com.mifan.article.domain.support.Rank;
import com.mifan.article.feign.FeignConfiguration;
import org.moonframework.web.jsonapi.Data;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author ZYW
 * 2018-02-01
 */
@FeignClient(name = "micro-service-support", configuration = FeignConfiguration.class)
public interface RankClient {
    @RequestMapping(method = GET, value = "/ranks/scores")
    ResponseEntity<Data<List<Rank>>> findPageWithScore(@RequestParam(value = "params", required = true) Map<String, Object> params);

    @RequestMapping(method = GET, value = "/ranks")
    ResponseEntity<Data<List<Topics>>> findPage(@RequestParam(value = "params", required = true) Map<String, Object> params);
}
