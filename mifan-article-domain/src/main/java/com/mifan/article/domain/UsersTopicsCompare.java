package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

import static org.moonframework.validation.ValidationGroups.Patch;
import static org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-12-12
 */
public class UsersTopicsCompare extends BaseEntity implements Cloneable {

    public static final String TABLE_NAME = "users_topics_compare";

    public static final String USER_ID = "user_id";
    public static final String TOPIC_ID = "topic_id";
    public static final String FOLDER_ID = "folder_id";

    private static final long serialVersionUID = -1611733462067927122L;

    @NotNull(groups = {Post.class}, message = "{NotNull.UsersTopicsCompare.userId}")
    @Null(groups = {Patch.class}, message = "{Null.UsersTopicsCompare.userId}")
    private Long userId;

    // @NotNull(groups = {Post.class}, message = "{NotNull.UsersTopicsCompare.topicId}")
    @Null(groups = {Patch.class}, message = "{Null.UsersTopicsCompare.topicId}")
    private Long topicId;

    @NotNull(groups = {Post.class}, message = "{NotNull.UsersTopicsCompare.folderId}")
    @Null(groups = {Patch.class}, message = "{Null.UsersTopicsCompare.folderId}")
    private Long folderId;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.UsersTopicsCompare.enabled}")
    private Integer enabled;

    //

    private Topics topic;

    private List<Long> topicIds;

    public UsersTopicsCompare() {
    }

    public UsersTopicsCompare(Long id) {
        super(id);
    }

    @Override
    public UsersTopicsCompare clone() throws CloneNotSupportedException {
        return (UsersTopicsCompare) super.clone();
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
    public Long getFolderId() {
        return folderId;
    }

    /**
     * @param folderId
     */
    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Topics getTopic() {
        return topic;
    }

    public void setTopic(Topics topic) {
        this.topic = topic;
    }

    public List<Long> getTopicIds() {
        return topicIds;
    }

    public void setTopicIds(List<Long> topicIds) {
        this.topicIds = topicIds;
    }
}
