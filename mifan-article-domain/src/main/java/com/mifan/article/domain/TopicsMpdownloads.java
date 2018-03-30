package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-03-21
 */
public class TopicsMpdownloads extends BaseEntity {

    public static final String TABLE_NAME = "topics_mpdownloads";

    public static final String TOPIC_ID = "topic_id";
    public static final String MP_DOWNLOAD_ID = "mp_download_id";

    private static final long serialVersionUID = -7181462509895627177L;

    private Long topicId;
    private Long mpDownloadId;
    
    public TopicsMpdownloads() {
    }

    public TopicsMpdownloads(Long id) {
        super(id);
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
    public Long getMpDownloadId() {
        return mpDownloadId;
    }

    /**
     * @param mpDownloadId 
     */
    public void setMpDownloadId(Long mpDownloadId) {
        this.mpDownloadId = mpDownloadId;
    }

}
