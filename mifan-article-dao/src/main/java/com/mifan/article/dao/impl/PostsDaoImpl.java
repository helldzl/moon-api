package com.mifan.article.dao.impl;

import com.mifan.article.dao.PostsDao;
import com.mifan.article.domain.Posts;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
@Repository
public class PostsDaoImpl extends AbstractBaseDao<Posts> implements PostsDao {

    @Override
    public Page<Posts> findPageHumanTranslate(Posts entity, Pageable pageable) {
        String findPageHumanTranslate = entityClass.getName() + "." + "findPageHumanTranslate";
        Map<String, Object> map = pageMap(pageable);
        map.put("entity", entity);
        List<Posts> content = session.selectList(findPageHumanTranslate, map);
        return new PageImpl<>(content, pageable, countHumanTranslate(entity));
    }

    private long countHumanTranslate(Posts entity) {
        String countHumanTranslate = entityClass.getName() + "." + "countHumanTranslate";
        Map<String, Object> map = new HashMap<>(16);
        map.put("entity", entity);
        return session.selectOne(countHumanTranslate, map);

    }
}
