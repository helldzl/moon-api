package com.mifan.article.dao;

import com.mifan.article.domain.TopicsFetch;

import java.util.List;

import org.moonframework.model.mybatis.repository.BaseDao;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public interface TopicsFetchDao extends BaseDao<TopicsFetch> {
    List<TopicsFetch> mpHotNews(Long[] seeds,int size,String mydate);
}
