package com.mifan.article.web;

import com.alibaba.fastjson.JSONArray;
import com.mifan.article.domain.SpiderStatistics;
import com.mifan.article.domain.Topics;
import com.mifan.article.domain.TopicsFetch;
import com.mifan.article.service.SpiderService;
import com.mifan.article.service.TopicsFetchService;
import com.mifan.article.service.TopicsService;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by LiuKai on 2017/11/9.
 */
@RestController
@RequestMapping("/spider")
public class SpiderStatisticsController extends RestfulController<Topics> {

    @Autowired
    private TopicsService topicsService;
    @Autowired
    private SpiderService spiderService;
    @Autowired
    private TopicsFetchService topicsFetchService;

    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false, name = "filter[priortime]") String priortime,
            @RequestParam(required = false, name = "filter[latertime]") String latertime) {

        HttpServletRequest request = getHttpServletRequest();

        Page<SpiderStatistics> spiderStatisticses = topicsService.findSpiderStatisticsByTime(priortime, latertime, page, size);

        return ResponseEntity.ok(Responses.builder().page(spiderStatisticses, "/spider/statistics", request.getParameterMap()).data(spiderStatisticses.getContent()));
    }

    @RequestMapping(value = "/rabbitmq/connections", method = RequestMethod.GET)
    public ResponseEntity<Response> doGetConnections() {

        JSONArray ConnectionsInfo = spiderService.getRabbitMqConnectionsInfo();
        return ResponseEntity.ok(Responses.builder().data(ConnectionsInfo));
    }

    @RequestMapping(value = "/statistics/topicsfetch/{seedId}", method = RequestMethod.GET)
    public ResponseEntity<Response> doGetTopicFetchBySeedId(
            @PathVariable Long seedId,
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size ) {

        HttpServletRequest request = getHttpServletRequest();

        Page<TopicsFetch> topicsFetchPage = topicsFetchService.findAll(
                Restrictions.eq(TopicsFetch.SEED_ID, seedId),
                Pages.builder().page(page).size(size).sort(Pages.sortBuilder().add(TopicsFetch.ID, false).build()).build(),
                Fields.builder().add(TopicsFetch.ID).add(TopicsFetch.TOPIC_ID).add(TopicsFetch.SEED_ID).add(TopicsFetch.ORIGIN).build(),
                true
        );
        return ResponseEntity.ok(Responses.builder().page(topicsFetchPage, "/spider/statistics/topicsfetch/" + seedId, request.getParameterMap()).data(topicsFetchPage.getContent()));
    }
}
