package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class TopicsRelated extends BaseEntity {

    public static final String TABLE_NAME = "topics_related";

    public static final String TOPIC_ID = "topic_id";
    public static final String TARGET_ID = "target_id";
    public static final String TITLE = "title";
    public static final String TITLE_HASH = "title_hash";

    private static final long serialVersionUID = -6461158557728120356L;

    private Long topicId;
    private Long targetId;
    private String title;
    private Long titleHash;

    public TopicsRelated() {
    }

    public TopicsRelated(Long id) {
        super(id);
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
     * @return 目标ID
     */
    public Long getTargetId() {
        return targetId;
    }

    /**
     * @param targetId 目标ID
     */
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
    /**
     * @return 标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * @return 标题HASH
     */
    public Long getTitleHash() {
        return titleHash;
    }

    /**
     * @param titleHash 标题HASH
     */
    public void setTitleHash(Long titleHash) {
        this.titleHash = titleHash;
    }

}
