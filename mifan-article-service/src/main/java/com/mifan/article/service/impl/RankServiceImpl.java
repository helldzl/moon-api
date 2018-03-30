package com.mifan.article.service.impl;

import com.mifan.article.domain.Topics;
import com.mifan.article.domain.support.Rank;
import com.mifan.article.feign.support.RankClient;
import com.mifan.article.service.RankService;
import io.jsonwebtoken.lang.Collections;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.feign.Feigns;
import org.moonframework.web.jsonapi.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ZYW
 */
@Service
public class RankServiceImpl implements RankService {

    @Resource
    private RedisTemplate<String, Long> redisTemplate;

    @Autowired
    private RankClient rankClient;

    @Override
    public ResponseEntity<Data<List<Topics>>> findPage(String key, int page, int size) {
        ResponseEntity<Data<List<Topics>>> response = rankClient.findPage(Feigns.params()
                .page(p -> p.page(page).size(size))
                .filter("key", key)
                .build());
        if (response.getStatusCode().is2xxSuccessful()) {
            List<Topics> topics = response.getBody().getData();
            if (!Collections.isEmpty(topics)) {
                List<Long> topicIds = topics.stream().map(Topics::getId).collect(Collectors.toList());
                Map<Long, Topics> map = Services.findAll(Topics.class,
                        topicIds,
                        Fields.builder().add(Topics.ID).add(Topics.TITLE).build())
                        .stream().collect(Collectors.toMap(Topics::getId, Function.identity()));
                topics.forEach(t -> t.setTitle(map.get(t.getId()).getTitle()));
            }

        }
        return response;
    }
    /*@Override
    public void updateRanking(String key,Integer type) {
        
    }*/

    @Override
    public ResponseEntity<Data<List<Rank>>> findPageWithScore(String key, int page, int size) {
        Feigns.asRemote();
        ResponseEntity<Data<List<Rank>>> response = rankClient.findPageWithScore(Feigns.params()
                .page(p -> p.page(page).size(size))
                .filter("key", key)
                .build());
        if (response.getStatusCode().is2xxSuccessful()) {
            List<Rank> ranks = response.getBody().getData();
            if (!Collections.isEmpty(ranks)) {
                List<Long> topicIds = ranks.stream().map(Rank::getTopicId).collect(Collectors.toList());
                Map<Long, Topics> map = Services.findAll(Topics.class, topicIds, Fields.builder().add(Topics.ID).add(Topics.TITLE).build()).stream().collect(Collectors.toMap(BaseEntity::getId, topic -> topic));
                for (Rank rank : ranks) {
                    if (map.containsKey(rank.getTopicId())) {
                        rank.setTitle(map.get(rank.getTopicId()).getTitle());
                    } else {
                        rank.setTitle("");
                    }
                }
            }

        }
        return response;
    }
}
