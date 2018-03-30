package com.mifan.article.article;

import com.mifan.article.AbstractTests;
import com.mifan.article.dao.TopicsDao;
import com.mifan.article.domain.Posts;
import com.mifan.article.domain.Seeds;
import com.mifan.article.domain.SpiderStatistics;
import com.mifan.article.domain.Topics;
import com.mifan.article.domain.support.Brands;
import com.mifan.article.service.BrandsService;
import com.mifan.article.service.TopicsService;
import org.junit.Test;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheManager;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/14
 */
public class TopicsServiceTests extends AbstractTests {
    @Autowired
    private TopicsDao topicsDao;

    @Autowired
    private RedisCacheManager manager;

    @Autowired
    public EhCacheCacheManager ehCacheCacheManager;

    @Autowired
    private BrandsService service;

    @Autowired
    private TopicsService topicsService;

    @Test
    public void saveTopics() throws Exception {
        Posts post = new Posts();
        post.setTopicId(1L);

        Topics topic = new Topics(1L);
        topic.setForumId(1L);
        topic.setPost(post);
        topicsService.save(topic);
        topicsService.save(topic);
        topicsService.save(topic);
        System.out.println();
        System.out.println();

        Thread.sleep(10000000);
        System.out.println();
    }

    @Test
    public void testTopics2() {
        Brands brand = service.findBrand("2box");
        brand = service.findBrand("2box2");
    }

    @Test
    public void testTopics() {
        Topics topics = Services.queryForObject(Topics.class, 27398L, null);
        System.out.println(topics);
    }

    @Test
    public void testCache() {
        Services.findOne(Seeds.class, 1L);
        Services.findOne(Seeds.class, 1L);
        Services.findOne(Seeds.class, 1L);
        Services.findOne(Seeds.class, 2L);
        System.out.println();
    }

    @Test
    public void testTopic() {
        Pageable pageable = Pages.builder().page(2).size(5).build();
        Page<SpiderStatistics> spiderStatisticsList = topicsDao.findSpiderStatisticsByTime("2017-01-09 23:59:59", "2017-11-08 00:00:00", pageable, null);
        for (SpiderStatistics s : spiderStatisticsList) {
            System.out.println(s.toString());
        }
        System.out.println(spiderStatisticsList.getContent().size());
    }
}
