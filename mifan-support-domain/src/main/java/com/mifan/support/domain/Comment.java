package com.mifan.support.domain;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.validation.ValidationGroups.Post;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017-05-22
 */
public class Comment extends BaseEntity {

    public static final String TABLE_NAME = "comment";

    public static final String THEME_ID = "theme_id";
    public static final String CONF_ID = "conf_id";
    public static final String TOP_ID = "top_id";
    public static final String RE_USER_ID = "re_user_id";
    public static final String REPLAY_ID = "replay_id";
    public static final String CONTENT = "content";
    public static final String IS_ANONYMOUS = "is_anonymous";
    public static final String STATE = "state";
    public static final String CREATOR = "creator";

    private static final long serialVersionUID = -7350076178552890786L;
    
    @NotNull(groups = Post.class, message = "{NotNull.Comment.themeId}")
    private Long themeId;
    @NotNull(groups = Post.class, message = "{NotNull.Comment.confId}")
    private Long confId;
    @NotNull(groups = Post.class, message = "{NotNull.Comment.topId}")
    private Long topId;
    private Long reUserId;
    private Long replayId;
    @NotBlank(groups = Post.class, message = "{NotNull.Comment.content}")
    private String content;
    @NotNull(groups = Post.class, message = "{NotNull.Comment.isAnonymous}")
    private Integer isAnonymous;
    private Integer state;
    
    private Long[] tagIds;//评论中使用的标签 
    
    private Long praiseCount;//点赞计数
    
    private Long _praiseCount;//踩计数
    
    private Long replayCount;//回复数
    
    private Integer currentScore;//当前登录人的赞踩状态
    
    private List<Comment> hotComments = Collections.emptyList();//当前一级评论的热门评论
    
    private List<Comment> reComments = Collections.emptyList();//当前一级评论的二级评论
    
    private String nickName;//该评论的用户昵称
    
    private String userAvatar;//该评论的用户头像
    
    private Integer gender;//该评论的用户性别 
    
    private String reUserNickName;//被回复的用户昵称

    public Comment() {
    }
    

    public Comment(Long confId, Long topId) {
        super();
        this.confId = confId;
        this.topId = topId;
    }


    public Comment(Long id) {
        super(id);
    }

    /**
     * @return 主题标识
     */
    public Long getThemeId() {
        return themeId;
    }

    /**
     * @param themeId 主题标识
     */
    public void setThemeId(Long themeId) {
        this.themeId = themeId;
    }
    /**
     * @return 评论配置标识
     */
    public Long getConfId() {
        return confId;
    }

    /**
     * @param confId 评论配置标识
     */
    public void setConfId(Long confId) {
        this.confId = confId;
    }
    /**
     * @return 一级评论标识
     */
    public Long getTopId() {
        return topId;
    }

    /**
     * @param topId 一级评论标识
     */
    public void setTopId(Long topId) {
        this.topId = topId;
    }
    /**
     * @return 被回复的用户的标识
     */
    public Long getReUserId() {
        return reUserId;
    }

    /**
     * @param reUserId 被回复的用户的标识
     */
    public void setReUserId(Long reUserId) {
        this.reUserId = reUserId;
    }
    /**
     * @return 被回复的comment的标识
     */
    public Long getReplayId() {
        return replayId;
    }

    /**
     * @param replayId 被回复的comment的标识
     */
    public void setReplayId(Long replayId) {
        this.replayId = replayId;
    }
    /**
     * @return 评论正文
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content 评论正文
     */
    public void setContent(String content) {
        this.content = content;
    }
    /**
     * @return 是否匿名
     */
    public Integer getIsAnonymous() {
        return isAnonymous;
    }

    /**
     * @param isAnonymous 是否匿名
     */
    public void setIsAnonymous(Integer isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
    /**
     * @return 状态，1：正常，0：不合法
     */
    public Integer getState() {
        return state;
    }

    /**
     * @param state 状态，1：正常，0：不合法
     */
    public void setState(Integer state) {
        this.state = state;
    }

    public Long[] getTagIds() {
        return tagIds;
    }

    public void setTagIds(Long[] tagIds) {
        this.tagIds = tagIds;
    }

    public Long getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(Long praiseCount) {
        this.praiseCount = praiseCount;
    }

    public Long get_praiseCount() {
        return _praiseCount;
    }

    public void set_praiseCount(Long _praiseCount) {
        this._praiseCount = _praiseCount;
    }

    public Long getReplayCount() {
        return replayCount;
    }

    public void setReplayCount(Long replayCount) {
        this.replayCount = replayCount;
    }

    

    public List<Comment> getHotComments() {
        return hotComments;
    }


    public void setHotComments(List<Comment> hotComments) {
        this.hotComments = hotComments;
    }


    public Integer getCurrentScore() {
        return currentScore;
    }


    public void setCurrentScore(Integer currentScore) {
        this.currentScore = currentScore;
    }


    public String getNickName() {
        return nickName;
    }


    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


    public String getUserAvatar() {
        return userAvatar;
    }


    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }


    public Integer getGender() {
        return gender;
    }


    public void setGender(Integer gender) {
        this.gender = gender;
    }


    public String getReUserNickName() {
        return reUserNickName;
    }


    public void setReUserNickName(String reUserNickName) {
        this.reUserNickName = reUserNickName;
    }


    public List<Comment> getReComments() {
        return reComments;
    }


    public void setReComments(List<Comment> reComments) {
        this.reComments = reComments;
    }
    
}
