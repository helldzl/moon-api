package com.mifan.article.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;

import java.util.Date;
import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class TopicsDocument extends BaseEntity {

    public static Iterable<? extends Field> DEFAULT_FIELDS = Fields.builder()
            .add(TopicsDocument.POST_DATE)
            .add(TopicsDocument.AUTHOR)
            .add(TopicsDocument.BRAND)
            .build();

    public static final String TABLE_NAME = "topics_document";

    public static final String TOPIC_ID = "topic_id";
    public static final String POST_DATE = "post_date";
    public static final String AUTHOR = "author";
    public static final String BRAND = "brand";

    private static final long serialVersionUID = 2904430520612956042L;

    private Long topicId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date postDate;
    private String author;
    private String brand;

    private List<String> brands;

    public TopicsDocument() {
    }

    public TopicsDocument(Long id) {
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
     * @return 发表日期
     */
    public Date getPostDate() {
        return postDate;
    }

    /**
     * @param postDate 发表日期
     */
    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    /**
     * @return 作者
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author 作者
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return 品牌
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @param brand 品牌
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    public List<String> getBrands() {
        return brands;
    }

    public void setBrands(List<String> brands) {
        this.brands = brands;
    }
}
