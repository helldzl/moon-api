package com.mifan.article.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;
import org.moonframework.model.mybatis.domain.BaseEntity;

import com.mifan.article.domain.support.ValidationGroups.MpPost;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-03-21
 */
public class TopicsMp extends BaseEntity {

    public static final String TABLE_NAME = "topics_mp";

    public static final String TYPE = "type";
    public static final String TOPIC_ID = "topic_id";
    public static final String UP_TIMES = "up_times";
    public static final String MP_CATEGORY_ID = "mp_category_id";

    private static final long serialVersionUID = 8203792162133157910L;

    @NotNull(groups = {MpPost.class},message = "{NotNull.topicsMp.type}")
    @Range(groups = {MpPost.class}, message = "{Error.topicsMp.type}", min = 0,max = 2)
    private Integer type;
    private Long topicId;
    private Integer upTimes;
    private Long mpCategoryId;

    public TopicsMp() {
    }

    public TopicsMp(Long id) {
        super(id);
    }

    /**
     * @return 类型：0：图文，1：图文+视频，2：视频
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type 类型：0：图文，1：图文+视频，2：视频
     */
    public void setType(Integer type) {
        this.type = type;
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
     * @return up次数
     */
    public Integer getUpTimes() {
        return upTimes;
    }

    /**
     * @param upTimes up次数
     */
    public void setUpTimes(Integer upTimes) {
        this.upTimes = upTimes;
    }
    /**
     * @return 美频分类
     */
    public Long getMpCategoryId() {
        return mpCategoryId;
    }

    /**
     * @param mpCategoryId 美频分类
     */
    public void setMpCategoryId(Long mpCategoryId) {
        this.mpCategoryId = mpCategoryId;
    }

}
