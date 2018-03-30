package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

import javax.validation.constraints.Null;

import static org.moonframework.validation.ValidationGroups.Patch;
import static org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-08
 */
public class UsersTopicsFavorite extends BaseEntity {

    public static final String TABLE_NAME = "users_topics_favorite";

    public static final String USER_ID = "user_id";
    public static final String TOPIC_ID = "topic_id";
    public static final String FORUM_ID = "forum_id";

    private static final long serialVersionUID = -1109103981465898366L;

    private Long userId;
    private Long topicId;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.UsersTopicsFavorite.forumId}")
    private Long forumId;

    public UsersTopicsFavorite() {
    }

    public UsersTopicsFavorite(Long id) {
        super(id);
    }

    /**
     * @return
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return
     */
    public Long getTopicId() {
        return topicId;
    }

    /**
     * @param topicId
     */
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    /**
     * @return
     */
    public Long getForumId() {
        return forumId;
    }

    /**
     * @param forumId
     */
    public void setForumId(Long forumId) {
        this.forumId = forumId;
    }

}
