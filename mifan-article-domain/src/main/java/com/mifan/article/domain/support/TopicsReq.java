/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.article.domain.support.TopicsReq
 *
 * @description:用作findPage的查询条件
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年8月25日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.article.domain.support;

import javax.validation.constraints.NotNull;

import org.moonframework.validation.ValidationGroups.Get;

/**
 * @author ZYW
 *
 */
public class TopicsReq {
    private Long id;
    private String title;
    @NotNull(groups = Get.class, message = "{NotNull.topics.forumId}")
    private Long forumId;
    private Integer locked;
    private Integer moderated;
    private Long currentUserId;
    private Long creator;
    private Long modifier;
    private int size = 10;
    private int num = 1;
    
    public TopicsReq(Long id, String title) {
        super();
        this.id = id;
        if(title != null && title.trim() == ""){
            this.title = null;
        }else{
            this.title = title;
        }
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Long getForumId() {
        return forumId;
    }
    public void setForumId(Long forumId) {
        this.forumId = forumId;
    }
    public Integer getLocked() {
        return locked;
    }
    public void setLocked(Integer locked) {
        this.locked = locked;
    }
    public Integer getModerated() {
        return moderated;
    }
    public void setModerated(Integer moderated) {
        this.moderated = moderated;
    }
    public Long getCurrentUserId() {
        return currentUserId;
    }
    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
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
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public int getNum() {
        return num;
    }
    public void setNum(int num) {
        this.num = num;
    }
    
}
