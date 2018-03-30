package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class TopicsAttachments extends BaseEntity {

    public static final String TABLE_NAME = "topics_attachments";

    public static final String TOPIC_ID = "topic_id";
    public static final String ATTACHMENT_ID = "attachment_id";

    private static final long serialVersionUID = -3305559524057749956L;

    private Long topicId;
    private Long attachmentId;

    public TopicsAttachments() {
    }

    public TopicsAttachments(Long id) {
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
     * @return 附件ID
     */
    public Long getAttachmentId() {
        return attachmentId;
    }

    /**
     * @param attachmentId 附件ID
     */
    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

}
