package com.mifan.article.batch.impl;

import com.mifan.article.domain.Topics;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by LiuKai on 2017/12/27.
 */
@Component
public class TopicsUpdateTimerTask {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${application.scheduler.enable.update.timer:false}")
    private boolean enableTimer;

    //每5分钟执行一次
    @Scheduled(initialDelay = 1 * 1000, fixedDelay = 60 * 1000 * 5)
    public void updateTopics() {
        if (!enableTimer) {
            return;
        }

        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        if (redisTemplate.hasKey("topicIds") != false && redisTemplate.keys("topicIds").size() > 0) {
            do {
                Set<Topics> topicses = new HashSet<>();
                do {
                    Long id = Long.parseLong(setOperations.pop("topicIds"));
                    Topics topics = new Topics();
                    topics.setId(id);
                    topicses.add(topics);
                } while (topicses.size() < 100 && redisTemplate.keys("topicIds").size() != 0);
                Services.update(Topics.class, topicses);
            } while (redisTemplate.keys("topicIds").size() > 0);
        }
    }
}
