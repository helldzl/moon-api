package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

import javax.validation.constraints.Size;

import static org.moonframework.validation.ValidationGroups.Patch;
import static org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class UsersTopicsHide extends BaseEntity {

    public static final String TABLE_NAME = "users_topics_hide";

    public static final String USER_ID = "user_id";
    public static final String TOPIC_ID = "topic_id";
    public static final String REMARK = "remark";

    private static final long serialVersionUID = -7945932125208382874L;

    private Long userId;
    private Long topicId;

    @Size(min = 2, max = 140, groups = {
            Post.class,
            Patch.class
    }, message = "{Size.UsersTopicsHide.remark}")
    private String remark;

    public UsersTopicsHide() {
    }

    public UsersTopicsHide(Long id) {
        super(id);
    }

    /**
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId 用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
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
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

}
