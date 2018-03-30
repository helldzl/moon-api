package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class TopicsFetch extends BaseEntity {

    public static Iterable<? extends Field> DEFAULT_FIELDS = Fields.builder()
            .add(TopicsFetch.ID)
            .add(TopicsFetch.TOPIC_ID)
            .add(TopicsFetch.SEED_ID)
            .add(TopicsFetch.ORIGIN)
            .add(TopicsFetch.RATING)
            .add(TopicsFetch.REVIEWS)
            .add(TopicsFetch.THUMBS_UP)
            .build();

    public static final String TABLE_NAME = "topics_fetch";

    public static final String TOPIC_ID = "topic_id";
    public static final String SEED_ID = "seed_id";
    public static final String ORIGIN = "origin";
    public static final String ORIGIN_HASH = "origin_hash";
    public static final String RATING = "rating";
    public static final String REVIEWS = "reviews";
    public static final String THUMBS_UP = "thumbs_up";

    private static final long serialVersionUID = -3320771078634654780L;

    private Long topicId;
    private Long seedId;
    private String origin;
    private Long originHash;
    private Double rating;
    private Integer reviews;
    private Integer thumbsUp;

    private String host;
    private String source;
    private String image;
    private Long channelId;

    public TopicsFetch() {
    }

    public TopicsFetch(Long id) {
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
     * @return 种子ID
     */
    public Long getSeedId() {
        return seedId;
    }

    /**
     * @param seedId 种子ID
     */
    public void setSeedId(Long seedId) {
        this.seedId = seedId;
    }

    /**
     * @return 来源URL
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * @param origin 来源URL
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * @return 来源URL HASH
     */
    public Long getOriginHash() {
        return originHash;
    }

    /**
     * @param originHash 来源URL HASH
     */
    public void setOriginHash(Long originHash) {
        this.originHash = originHash;
    }

    /**
     * @return 评分, 区间[0, 1]
     */
    public Double getRating() {
        return rating;
    }

    /**
     * @param rating 评分, 区间[0, 1]
     */
    public void setRating(Double rating) {
        this.rating = rating;
    }

    /**
     * @return 检阅数
     */
    public Integer getReviews() {
        return reviews;
    }

    /**
     * @param reviews 检阅数
     */
    public void setReviews(Integer reviews) {
        this.reviews = reviews;
    }

    /**
     * @return up次数
     */
    public Integer getThumbsUp() {
        return thumbsUp;
    }

    /**
     * @param thumbsUp up次数
     */
    public void setThumbsUp(Integer thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
}
