package com.mifan.article.domain;

import javax.validation.constraints.NotNull;

import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.validation.ValidationGroups.Post;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-08-16
 */
public class TopicsClustering extends BaseEntity implements Cloneable{

    public static final String TABLE_NAME = "topics_clustering";

    public static final String TOPIC_ID = "topic_id";
    public static final String TARGET_ID = "target_id";
    public static final String SCORE = "score";
    public static final String TYPE = "type";

    private static final long serialVersionUID = -8389204920962855996L;

    @NotNull(groups = Post.class, message = "{NotNull.TopicsClustering.topicId}")
    private Long topicId;
    @NotNull(groups = Post.class, message = "{NotNull.TopicsClustering.targetId}")
    private Long targetId;
    private Double score;
    private Integer type;

    public TopicsClustering() {
    }

    public TopicsClustering(Long id) {
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
     * @return 评分
     */
    public Double getScore() {
        return score;
    }

    /**
     * @param score 评分
     */
    public void setScore(Double score) {
        this.score = score;
    }
    /**
     * @return 类型，0：机器学习；1：人工干预
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type 类型，0：机器学习；1：人工干预
     */
    public void setType(Integer type) {
        this.type = type;
    }
    public enum ClusteringType {

        ROBOT(0, "机器学习类型"),
        PEOPLE(1, "人工干预");

        private final int index;
        private final String name;

        ClusteringType(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }
    }
    @Override  
    public Object clone() {
        TopicsClustering clus = null;
        try{
            clus = (TopicsClustering)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clus;
    }
}
