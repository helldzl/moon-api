package com.mifan.article.domain.support;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author ZYW
 *
 */
public class Comment{
	private Long id;
	private Long confId;
    private Long themeId;
    private Long topId;
    private Long replayId;
    private Long reUserId;
    private String content;
    private Integer isAnonymous;
    private Long[] tagIds;
    
    private Integer enabled;
    private Long creator;
    private Long modifier;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modified;
    
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
    
	public Long getConfId() {
		return confId;
	}
	public void setConfId(Long confId) {
		this.confId = confId;
	}
	public Long getThemeId() {
		return themeId;
	}
	public void setThemeId(Long themeId) {
		this.themeId = themeId;
	}
	public Long getTopId() {
		return topId;
	}
	public void setTopId(Long topId) {
		this.topId = topId;
	}
	public Long getReplayId() {
		return replayId;
	}
	public void setReplayId(Long replayId) {
		this.replayId = replayId;
	}
	public Long getReUserId() {
		return reUserId;
	}
	public void setReUserId(Long reUserId) {
		this.reUserId = reUserId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getIsAnonymous() {
		return isAnonymous;
	}
	public void setIsAnonymous(Integer isAnonymous) {
		this.isAnonymous = isAnonymous;
	}
	public Long[] getTagIds() {
		return tagIds;
	}
	public void setTagIds(Long[] tagIds) {
		this.tagIds = tagIds;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getEnabled() {
		return enabled;
	}
	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}
	public Long getCreator() {
		return creator;
	}
	public void setCreator(Long creator) {
		this.creator = creator;
	}
	public Long getModifier() {
		return modifier;
	}
	public void setModifier(Long modifier) {
		this.modifier = modifier;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
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
	public Integer getCurrentScore() {
		return currentScore;
	}
	public void setCurrentScore(Integer currentScore) {
		this.currentScore = currentScore;
	}
	public List<Comment> getHotComments() {
		return hotComments;
	}
	public void setHotComments(List<Comment> hotComments) {
		this.hotComments = hotComments;
	}
	public List<Comment> getReComments() {
		return reComments;
	}
	public void setReComments(List<Comment> reComments) {
		this.reComments = reComments;
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
}
