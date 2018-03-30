package com.mifan.article.service.impl;

import com.mifan.article.cache.TopicsCache;
import com.mifan.article.dao.UsersTopicsLikeDao;
import com.mifan.article.domain.Topics;
import com.mifan.article.domain.UsersTopicsLike;
import com.mifan.article.service.UsersTopicsLikeService;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pair;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Service
public class UsersTopicsLikeServiceImpl extends AbstractBaseService<UsersTopicsLike, UsersTopicsLikeDao> implements UsersTopicsLikeService {

    @Autowired
    private TopicsCache topicsCache;

    /**
     * @param insert insert
     * @param update update
     * @param <S>    S
     * @return
     */
    @Override
    public <S extends UsersTopicsLike> int saveOrUpdate(S insert, S update) {
        if (!Services.exists(Topics.class, insert.getTopicId())) {
            return 0;
        }

        Long userId = insert.getUserId();
        Long topicId = insert.getTopicId();
        boolean up = insert.getUp() == 1;

        UsersTopicsLike unique = new UsersTopicsLike();
        unique.setUserId(userId);
        unique.setTopicId(topicId);
        return Services.saveOrUpdate(
                UsersTopicsLike.class,
                unique,
                insert,
                update,
                Restrictions.ne(UsersTopicsLike.UP, insert.getUp()),
                action -> {
                    if (Services.Action.NONE == action) {
                        return;
                    }

                    Pair.PairBuilder builder = Pair.builder();
                    if (up) {
                        builder.add(Topics.THUMBS_UP, 1);
                        topicsCache.increment(topicId, TopicsCache.Statistics.THUMBS_UP);
                        if (Services.Action.UPDATE_BY_CRITERION == action) {
                            builder.add(Topics.THUMBS_DOWN, -1);
                            topicsCache.increment(topicId, TopicsCache.Statistics.THUMBS_DOWN, -1);
                        }
                    } else {
                        builder.add(Topics.THUMBS_DOWN, 1);
                        topicsCache.increment(topicId, TopicsCache.Statistics.THUMBS_DOWN);
                        if (Services.Action.UPDATE_BY_CRITERION == action) {
                            builder.add(Topics.THUMBS_UP, -1);
                            topicsCache.increment(topicId, TopicsCache.Statistics.THUMBS_UP, -1);
                        }
                    }

                    if (Services.update(Topics.class, topicId, builder.build(), null) > 0) {
                        topicsCache.addLike(userId, topicId, up);
                    }
                });
    }

    @Override
    public int remove(Iterable<Long> ids) {
        return Services.remove(
                UsersTopicsLike.class,
                ids,
                Fields.builder()
                        .add(UsersTopicsLike.USER_ID)
                        .add(UsersTopicsLike.TOPIC_ID)
                        .add(UsersTopicsLike.UP).build(),
                like -> {
                    Pair.PairBuilder builder = Pair.builder();
                    Long userId = like.getUserId();
                    Long topicId = like.getTopicId();
                    boolean up = like.getUp() == 1;
                    if (up) {
                        builder.add(Topics.THUMBS_UP, -1);
                        topicsCache.increment(topicId, TopicsCache.Statistics.THUMBS_UP, -1);
                    } else {
                        builder.add(Topics.THUMBS_DOWN, -1);
                        topicsCache.increment(topicId, TopicsCache.Statistics.THUMBS_DOWN, -1);
                    }

                    if (Services.update(Topics.class, topicId, builder.build(), null) > 0) {
                        topicsCache.removeLiked(userId, topicId);
                    }
                });
    }

}
