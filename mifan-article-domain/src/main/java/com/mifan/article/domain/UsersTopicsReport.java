package com.mifan.article.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.moonframework.model.mybatis.domain.BaseEntity;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-05
 */
public class UsersTopicsReport extends BaseEntity {

    public static final String TABLE_NAME = "users_topics_report";

    public static final String USER_ID = "user_id";
    public static final String TOPIC_ID = "topic_id";
    public static final String REMARK = "remark";
    public static final String VOTES_OPTION_IDS = "votes_option_ids";

    private static final long serialVersionUID = 241867121395297157L;

    private Long userId;
    private Long topicId;
    private String remark;
    private String votesOptionIds;

    //
    private Long[] options;

    public UsersTopicsReport() {
    }

    public UsersTopicsReport(Long id) {
        super(id);
    }

    public Long[] getOptions() {
        return options;
    }

    public void setOptions(Long[] options) {
        this.options = options;
        if (options != null && options.length != 0) {
            this.votesOptionIds = String.join(",", Stream.of(options).map(Object::toString).collect(toList()));
        } else {
            this.votesOptionIds = null;
        }
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
     * @return
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return
     */
    @JsonIgnore
    public String getVotesOptionIds() {
        return votesOptionIds;
    }

    /**
     * @param votesOptionIds
     */
    public void setVotesOptionIds(String votesOptionIds) {
        this.votesOptionIds = votesOptionIds;
        if (votesOptionIds != null && !"".equals(votesOptionIds)) {
            this.options = Stream.of(votesOptionIds.split(",")).map(Long::valueOf).toArray(Long[]::new);
        } else {
            this.options = null;
        }
    }

}
