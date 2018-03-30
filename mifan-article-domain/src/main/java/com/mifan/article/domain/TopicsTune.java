package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-01-09
 */
public class TopicsTune extends BaseEntity {

    public static final String TABLE_NAME = "topics_tune";

    public static final String TOPIC_ID = "topic_id";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String AUTHOR = "author";
    public static final String BRAND = "brand";
    public static final String DOWNLOAD = "download";

    private static final long serialVersionUID = -4490361716331892036L;

    private Long topicId;
    private Integer type;
    private String name;
    private String author;
    private String brand;
    private String download;

    public TopicsTune() {
    }

    public TopicsTune(Long id) {
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
     * @return 3:新闻;5:视频;6:图片;7:音频;8:技术
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type 3:新闻;5:视频;6:图片;7:音频;8:技术
     */
    public void setType(Integer type) {
        this.type = type;
    }
    /**
     * @return 
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return 
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author 
     */
    public void setAuthor(String author) {
        this.author = author;
    }
    /**
     * @return 
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @param brand 
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }
    /**
     * @return 
     */
    public String getDownload() {
        return download;
    }

    /**
     * @param download 
     */
    public void setDownload(String download) {
        this.download = download;
    }

}
