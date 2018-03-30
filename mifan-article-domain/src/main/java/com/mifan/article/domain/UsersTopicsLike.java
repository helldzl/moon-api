package com.mifan.article.domain;

import org.hibernate.validator.constraints.Range;
import org.moonframework.model.mybatis.domain.BaseEntity;

import javax.validation.constraints.NotNull;

import static org.moonframework.validation.ValidationGroups.Patch;
import static org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class UsersTopicsLike extends BaseEntity {

    public static final String TABLE_NAME = "users_topics_like";

    public static final String USER_ID = "user_id";
    public static final String TOPIC_ID = "topic_id";
    public static final String UP = "up";

    private static final long serialVersionUID = 2599734341680165866L;

    private Long userId;
    private Long topicId;

    @NotNull(groups = {
            Post.class,
            Patch.class
    }, message = "{NotNull.UsersTopicsLike.up}")
    @Range(min = 0, max = 1, groups = {
            Post.class,
            Patch.class
    }, message = "{Range.UsersTopicsLike.up}")
    private Integer up;

    public UsersTopicsLike() {
    }

    public UsersTopicsLike(Long id) {
        super(id);
    }

    /**
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId 用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return 主题ID
     */
    public Long getTopicId() {
        return topicId;
    }

    /**
     * @param topicId 主题ID
     */
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    /**
     * @return 是否喜欢
     */
    public Integer getUp() {
        return up;
    }

    /**
     * @param up 是否喜欢
     */
    public void setUp(Integer up) {
        this.up = up;
    }

}
