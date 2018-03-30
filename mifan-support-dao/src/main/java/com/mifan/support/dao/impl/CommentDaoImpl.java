/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.support.dao.impl.CommentDaoImpl
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
package com.mifan.support.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.mifan.support.dao.CommentDao;
import com.mifan.support.domain.Comment;

/**
 * @author ZYW
 *
 */
@Repository
public class CommentDaoImpl extends AbstractBaseDao<Comment> implements CommentDao {
    
    @Override
    public Page<Comment> findPage(Long currentUserId,long[] themeIds,Comment entity, Pageable pageable) {
        String findPageWithPraise = entityClass.getName() + "." + "findPageWithPraise";
        entity.setEnabled(1);//count()方法用
        entity.setState(0);//count()方法用
        
        Map<String, Object> map = pageMap(pageable);
        map.put("entity", entity);
        map.put("currentUserId", currentUserId);
        map.put("themeIds", themeIds);
        List<Comment> content = session.selectList(findPageWithPraise, map);
        return new PageImpl<>(content, pageable, count(themeIds,entity));
    }
    
    private long count(long[] themeIds,Comment entity) {
        String countInThemeIds = entityClass.getName() + "." +"countInThemeIds";
        Map<String, Object> map = new HashMap<>(2);
        map.put("entity", entity);
        map.put("themeIds", themeIds);
        return session.selectOne(countInThemeIds, map);
        
    }

    @Override
    public List<Comment> findHot(Long currentUserId, long[] themeIds,Long confId) {
        String findHotByObject = entityClass.getName() + "." + "findHotByObject";
        Map<String,Object> map = new HashMap<String,Object>(3);
        map.put("currentUserId", currentUserId);
        map.put("themeIds", themeIds);
        map.put("confId", confId);
        List<Comment> content = session.selectList(findHotByObject, map);
        return content;
    }

    @Override
    public List<Comment> findHotRe(Long currentUserId, Long topId) {
        String findHotReByObject = entityClass.getName() + "." + "findHotReByObject";
        Map<String,Object> map = new HashMap<String,Object>(2);
        map.put("currentUserId", currentUserId);
        map.put("topId", topId);
        List<Comment> content = session.selectList(findHotReByObject, map);
        return content;
    }

}
