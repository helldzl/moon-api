package com.mifan.article.domain;


import java.util.List;

import javax.validation.constraints.NotNull;

import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.validation.ValidationGroups.Patch;
import org.moonframework.validation.ValidationGroups.Post;


/**
 * @author ZYW
 * @version 1.0
 * @since 2017-09-01
 */
public class HopeTranslateExtend extends BaseEntity {

    public static final String TABLE_NAME = "hope_translate_extend";

    public static final String TOPIC_ID = "topic_id";
    public static final String STATE = "state";
    public static final String FEED_BACK = "feed_back";
    public static final String WHO = "who";
    public static final String USER_COUNT = "user_count";

    private static final long serialVersionUID = 8486397479150866471L;

    @NotNull(groups = {Post.class}, message = "{NotNull.HopeTranslateExtend.topicId}")
    private Long topicId;
    private Integer state;
    @NotNull(groups = {Patch.class}, message = "{NotNull.HopeTranslateExtend.feedBack}")
    private String feedBack;
    private Long who;
    private Integer userCount;
    
    private String title;//主题标题
    
    private List<HopeTranslate> hopes;//希望翻译的用户列表

    public HopeTranslateExtend() {
    }

    public HopeTranslateExtend(Long id) {
        super(id);
    }

    /**
     * @return 主题标识
     */
    public Long getTopicId() {
        return topicId;
    }

    /**
     * @param topicId 主题标识
     */
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }
    /**
     * @return 状态，0：待翻1：已翻2：驳回
     */
    public Integer getState() {
        return state;
    }

    /**
     * @param state 状态，0：待翻1：已翻2：驳回
     */
    public void setState(Integer state) {
        this.state = state;
    }
    /**
     * @return 管理员反馈
     */
    public String getFeedBack() {
        return feedBack;
    }

    /**
     * @param feedBack 管理员反馈
     */
    public void setFeedBack(String feedBack) {
        this.feedBack = feedBack;
    }
    /**
     * @return 谁翻译的
     */
    public Long getWho() {
        return who;
    }

    /**
     * @param who 谁翻译的
     */
    public void setWho(Long who) {
        this.who = who;
    }
    /**
     * @return 希望翻译计数
     */
    public Integer getUserCount() {
        return userCount;
    }

    /**
     * @param userCount 希望翻译计数
     */
    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HopeTranslate> getHopes() {
        return hopes;
    }

    public void setHopes(List<HopeTranslate> hopes) {
        this.hopes = hopes;
    }



    public enum HopeState {

        ready(0, "待翻"),

        already(1, "已翻"),

        reject(2, "驳回");

        private final int index;
        private final String name;

        HopeState(int index, String name) {
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
}
