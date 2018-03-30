package com.mifan.article.article;

import com.mifan.article.AbstractTests;
import com.mifan.article.domain.Links;
import com.mifan.article.domain.Topics;
import com.mifan.article.service.BrandsService;
import com.mifan.article.service.TopicsService;
import com.mifan.article.service.WordDictionaryService;
import org.junit.Test;
import org.moonframework.model.mybatis.domain.Pair;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/12
 */
public class ServiceTests extends AbstractTests {

    @Autowired
    private BrandsService service;

    @Autowired
    private TopicsService topicsService;

    @Autowired
    @Qualifier("wordDictionaryServiceImpl")
    private WordDictionaryService wordDictionaryService;

    @Test
    public void testWordDictionary() {
        System.out.println();
        wordDictionaryService.dictionary();
    }

    @Test
    public void testTrans() {
        // Services.findOne(Links.class, 1L);

        Services.transactional(Links.class, () -> {
            Services.update(Links.class, new Links(1L));
            Services.update(Links.class, new Links(1L));
            return 1;
        });
        System.out.println();
    }

    @Test
    public void testTopics() {
//        Set<Long> set = new HashSet<>();
//        set.add(120001L);
//        set.add(100001L);
//        set.add(100003L);
//        Map<Long, Map<Attachments.ContentType, List<Attachments>>> attachment = topicsService.findAttachment(set);
//        System.out.println();

//        Set<Long> set = new HashSet<>();
//        set.add(1L);
//        set.add(2L);
//        set.add(3L);
//        Map<Long, Posts> post = topicsService.findPost(set);
//        System.out.println(post);

        System.out.println();
    }

    @Test
    public void testBrands() {
        service.findBrand("2box");
    }

    @Test
    public void testInsert() {
        Pair.PairBuilder increments = Pair.builder();
        Pair.PairBuilder fields = Pair.builder();

        increments.add(Topics.THUMBS_UP, 1);
        increments.add(Topics.THUMBS_DOWN, 5);
        fields.add(Topics.REPLIES, 12);
        fields.add(Topics.REVIEWS, 6);
        Services.update(Topics.class, 1L, increments.build(), fields.build());

        System.out.println();
    }

    public static void main(String[] args) {

    }
}
