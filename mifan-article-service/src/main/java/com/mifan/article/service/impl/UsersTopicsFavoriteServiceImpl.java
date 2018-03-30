package com.mifan.article.service.impl;

import com.mifan.article.cache.TopicsCache;
import com.mifan.article.dao.UsersTopicsFavoriteDao;
import com.mifan.article.domain.Topics;
import com.mifan.article.domain.UsersTopicsFavorite;
import com.mifan.article.service.UsersTopicsFavoriteService;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-02
 */
@Service
public class UsersTopicsFavoriteServiceImpl extends AbstractBaseService<UsersTopicsFavorite, UsersTopicsFavoriteDao> implements UsersTopicsFavoriteService {

    @Autowired
    private TopicsCache topicsCache;

    @Override
    public <S extends UsersTopicsFavorite> int saveOrUpdate(S insert, S update) {
        Topics one = new Topics(insert.getTopicId());
        one = Services.findOne(Topics.class, one, Fields.builder().add(Topics.FORUM_ID).build());
        if (one == null) {
            return 0;
        }

        insert.setForumId(one.getForumId());
        update.setForumId(one.getForumId());

        Long userId = insert.getUserId();
        Long topicId = insert.getTopicId();

        UsersTopicsFavorite unique = new UsersTopicsFavorite();
        unique.setUserId(userId);
        unique.setTopicId(topicId);
        return Services.saveOrUpdate(
                UsersTopicsFavorite.class,
                unique,
                insert,
                update,
                action -> topicsCache.addFavorite(userId, topicId));
    }

    @Override
    public int remove(Iterable<Long> ids) {
        return Services.remove(UsersTopicsFavorite.class, ids,
                Fields.builder()
                        .add(UsersTopicsFavorite.USER_ID)
                        .add(UsersTopicsFavorite.TOPIC_ID)
                        .build(),
                favorite -> topicsCache.removeFavorite(favorite.getUserId(), favorite.getTopicId()));
    }

}
