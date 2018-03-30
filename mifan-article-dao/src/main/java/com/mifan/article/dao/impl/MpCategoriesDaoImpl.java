package com.mifan.article.dao.impl;

import com.mifan.article.dao.MpCategoriesDao;
import com.mifan.article.domain.MpCategories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-03-21
 */
@Repository
public class MpCategoriesDaoImpl extends AbstractBaseDao<MpCategories> implements MpCategoriesDao {

    @Override
    public List<MpCategories> findByParentIdBrandId(Long parentId, Long brandId) {
        String mpHotNews = entityClass.getName() + "." + "findByParentIdBrandId";
        Map<String, Object> map = new HashMap<>(16);
        map.put("parentId", parentId);
        map.put("brandId", brandId);
        List<MpCategories> list = session.selectList(mpHotNews, map);
        return list;
    }
}
