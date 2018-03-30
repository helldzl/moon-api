/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.sku.service.CommentService
 *
 * @description:
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年5月22日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.support.service;

import java.util.List;
import java.util.Map;

import org.moonframework.model.mybatis.service.BaseService;
import org.springframework.data.domain.Page;

import com.mifan.support.domain.Comment;

/**
 * @author ZYW
 *
 */
public interface CommentService extends BaseService<Comment> {
    
    /**
     * 列表查询，并附带点赞统计
     * @param currentUserId
     * @param confId
     * @param themeIds 重复的主题id
     * @param topId
     * @param page
     * @param size
     * @return
     */
    Page<Comment> doGetPage(Long currentUserId,Long confId,long[] themeIds,Long topId,int page,int size);
    
    /**
     * 获取主题的统计信息（评论数，赞踩数，标签统计）
     * @param confId
     * @param themeId
     * @return
     */
    Map<String,Object> themeInfoCount(Long confId,Long themeId);
    
    /**
     * 获取主题的热门评论
     * @param currentUserId
     * @param confId
     * @param themeIds 重复的主题id
     * @return
     */
    List<Comment> findHotComments(Long currentUserId,Long confId,long[] themeIds);
}
