package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class TopicsBrand extends BaseEntity {

    public static final String TABLE_NAME = "topics_brand";

    public static final String TOPIC_ID = "topic_id";
    public static final String LOGO = "logo";

    private static final long serialVersionUID = -333160267538163776L;

    private Long topicId;
    private String logo;

    public TopicsBrand() {
    }

    public TopicsBrand(Long id) {
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
     * @return LOGO
     */
    public String getLogo() {
        return logo;
    }

    /**
     * @param logo LOGO
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }

}
