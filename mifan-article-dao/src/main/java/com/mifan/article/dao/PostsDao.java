package com.mifan.article.dao;

import com.mifan.article.domain.Posts;

import org.moonframework.model.mybatis.repository.BaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public interface PostsDao extends BaseDao<Posts> {
    Page<Posts> findPageHumanTranslate(Posts entity, Pageable pageable);
}
