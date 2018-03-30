package com.mifan.article.batch.impl;

import com.mifan.article.batch.ScheduledTask;
import com.mifan.article.domain.Posts;
import com.mifan.article.domain.PostsText;
import com.mifan.article.domain.support.Content;
import com.mifan.article.service.RestService;
import org.moonframework.core.util.ObjectMapperFactory;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Pages;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.jsonapi.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/8/5
 */
@Component
public class TranslationTask extends ScheduledTask {

    @Autowired
    private RestService restService;

    @Value("${application.scheduler.enable.translation:false}")
    private boolean enableTranslation;

    public TranslationTask() {
        super(3L);
    }

    @Scheduled(initialDelay = 10 * 1500, fixedRate = 10 * 24 * 3600 * 1000)
    @Override
    public void task() {
        super.task();
    }

    @Override
    protected void doTask(Date from, Date to) {
        if (!enableTranslation) {
            return;
        }
        Services.doList(Posts.class,
                from,
                to,
                null,
                Restrictions.and(
                        Restrictions.eq(Posts.PARENT_ID, 0),
                        Restrictions.eq(Posts.CREATOR, 0),
                        Restrictions.eq(Posts.ENABLED, 1)),
                this::doPage);
    }

    private void doPage(Page<Posts> page) {
        Set<Long> ids = page.getContent().stream().map(BaseEntity::getId).collect(toSet());
        Map<Long, PostsText> texts = Services.findAll(PostsText.class,
                Restrictions.in(BaseEntity.ID, ids.toArray()),
                Pages.builder().page(1).size(ids.size()).build(),
                false).getContent().stream().collect(toMap(BaseEntity::getId, postsText -> postsText));
        for (Posts post : page.getContent()) {
            try {
                post.text(texts.get(post.getId()));
                post(post);
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
    }

    private void post(Posts post) {
        List<Map<String, String>> features = post.getFeatures();

        PostsText text = new PostsText();
        text.setTitle(translate(post.getTitle()));
        text.setDescription(translate(post.getDescription()));
        text.setContent(translate(post.getContent()));
        if (!CollectionUtils.isEmpty(features)) {
            text.setFeature(ObjectMapperFactory.writeValueAsString(features.stream().map(this::function).collect(Collectors.toList())));
        }
        PostsText postsText = post.getPostsText();
        if (postsText != null) {
            // 不翻译
            text.setCategory(postsText.getCategory());
            text.setTag(postsText.getTag());
        }

        Posts translation = new Posts();
        translation.setPostsText(text);
        // save(post, translation);
    }

    private Map<String, String> function(Map<String, String> map) {
        map.replace("_name", translate(map.get("_value")));
        return map;
    }

    /**
     * 保存翻译
     */
    private void save(Posts parent, Posts translation) {
        translation.setParentId(parent.getId());
        translation.setTopicId(parent.getTopicId());
        translation.setPriority(50);                    // 机器翻译设值一个中等程度的显示优先级
        translation.setEnabled(1);                      // 机器翻译不需要审核, 直接设置为1
        translation.setCreator(0L);
        translation.setModifier(0L);
        // posts text
        Services.save(Posts.class, translation);
    }

    private String translate(String content) {
        if (content == null) {
            return null;
        }

        Map<String, String> map = new HashMap<>(16);
        map.put("content", content);
        Data<Content> result = this.restService.postForEntity(Content.class, "/api/translate", RestService.requestAsMap(map));
        Content data;
        if (result != null && (data = result.getData()) != null) {
            // logger.info(data::getTranslate);
            return data.getTranslate();
        }
        return content;
    }

}
