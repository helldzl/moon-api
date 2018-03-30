package com.mifan.article.dao.impl;

import com.mifan.article.dao.TopicsFetchDao;
import com.mifan.article.domain.TopicsFetch;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Repository
public class TopicsFetchDaoImpl extends AbstractBaseDao<TopicsFetch> implements TopicsFetchDao {

    @Override
    public List<TopicsFetch> mpHotNews(Long[] seeds, int size, String mydate) {
        String mpHotNews = entityClass.getName() + "." + "mpHotNews";
        Map<String, Object> map = new HashMap<>(16);
        map.put("seeds", seeds);
        map.put("size", size);
        map.put("mydate", mydate);
        List<TopicsFetch> list = session.selectList(mpHotNews, map);
        return list;
    }
}
