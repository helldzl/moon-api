/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.sku.dao.CommentDao
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
package com.mifan.support.dao;

import java.util.List;

import org.moonframework.model.mybatis.repository.BaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mifan.support.domain.Comment;

/**
 * @author ZYW
 *
 */
public interface CommentDao extends BaseDao<Comment>{

    Page<Comment> findPage(Long currentUserId,long[] themeIds,Comment entity, Pageable pageable);
    
    /**
     * 查询主题下的所有热门评论
     * @param currentUserId
     * @param themeIds
     * @param confId
     * @return
     */
    List<Comment> findHot(Long currentUserId,long[] themeIds,Long confId);
    
    /**
     * 查询某个一级评论的前两个热门回复
     * @param currentUserId
     * @param topId
     * @return
     */
    List<Comment> findHotRe(Long currentUserId,Long topId);
}
