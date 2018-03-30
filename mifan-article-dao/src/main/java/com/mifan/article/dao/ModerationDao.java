package com.mifan.article.dao;

import com.mifan.article.domain.Moderation;

import org.moonframework.model.mybatis.repository.BaseDao;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-02
 */
public interface ModerationDao extends BaseDao<Moderation> {
    int updateByPostId(Moderation entity);
}
